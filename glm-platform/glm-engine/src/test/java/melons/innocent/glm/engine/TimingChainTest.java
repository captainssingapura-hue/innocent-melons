package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The timing chains: each bank's wrapped round its sprocket on the crank and its two camshafts', twice as big, so the
 * cams turn half as fast; its links going round evenly, each on the chain's line, a pitch apart; run on as the crank
 * turns, the way its teeth go, a link a pitch on where the next was; and each chain, its guide and its tensioner clear
 * of everything in the engine, covered, but the sprockets it runs on.
 */
class TimingChainTest {

    private final EngineRange range = new EngineRange();

    @Test
    void wrappedRoundTheCranksSprocketAndItsCamsTwiceAsBig() {
        for (final EngineFigures e : range.all()) {
            for (int b = 0; b < e.banks(); b++) {
                final ChainFigures chain = new ChainFigures(e, b);
                assertEquals(2, (double) chain.camTeeth() / chain.crankTeeth(), 1e-9, "the cams half the crank's speed");
                final Belt belt = chain.belt();
                assertEquals(new Spot(0, 0), belt.wheels().getFirst().middle(), e.name() + " the crank's first");
                assertEquals(chain.links() * chain.pitch(), belt.length(), 1e-9);
                assertEquals(ChainFigures.PITCH, chain.pitch(), 0.02, e.name() + " its links near their pitch");
                assertTrue(chain.plane() < 0 && chain.plane() > new TimingCover(e).face(), "ahead of the block");
            }
        }
    }

    @Test
    void itsLinksOnItsLineAPitchApartRunOnTheWayTheCrankTurns() {
        final EngineFigures v6 = range.v6();
        final TimingChain chain = new TimingChain(v6, 1);
        final ChainFigures figures = new ChainFigures(v6, 1);
        final List<Mat4> rest = chain.places(0);
        assertEquals(figures.links(), rest.size());
        for (int i = 0; i < rest.size(); i++) {
            final Vec3 at = roller(rest.get(i));
            final Vec3 next = roller(rest.get((i + 1) % rest.size()));
            assertEquals(figures.pitch(), at.minus(next).length(), 0.05, "a pitch apart, along its line");
            assertEquals(figures.plane(), at.z(), 1e-9);
        }
        final double pitchTurned = Math.toDegrees(figures.pitch() / figures.crank());
        final List<Mat4> on = chain.places(pitchTurned);
        for (int i = 1; i < rest.size(); i++) {
            assertEquals(0, roller(on.get(i)).minus(roller(rest.get(i - 1))).length(), 1e-6,
                    "a link a pitch on where the one before it was, back round its line as the crank turns");
        }
    }

    @Test
    void eachChainItsGuideAndTensionerClearOfAllButItsSprockets() {
        final TurnedTouching touching = new TurnedTouching();
        for (final EngineFigures e : List.of(range.v2(), range.l4(), range.b4(), range.v6())) {
            final Assembled built = new Assembly(new EngineInternals(e).platform(true)).assembled();
            final List<Part> parts = built.placed().keySet().stream().map(p -> p.part()).toList();
            final List<String> touched = new ArrayList<>();
            for (final Part drive : parts.stream().filter(p -> p.name().contains(" chain")).toList()) {
                final boolean chain = drive.name().contains("timing chain") || drive.name().contains("cam chain");
                for (final Part other : parts) {
                    final boolean runsOn = chain && (other.name().contains("crankshaft")
                            || other.name().contains("camshaft") && sameBank(drive, other));
                    if (other != drive && !runsOn
                            && !touching.wheres(drive.form(), new Mat4(), other.form(), new Mat4()).isEmpty()) {
                        touched.add(drive.name() + " on " + other.name());
                    }
                }
            }
            assertEquals(List.of(), touched, e.name());
        }
    }

    /** Whether {@code a} and {@code b} are of the same bank, or the engine has but one. */
    private boolean sameBank(final Part a, final Part b) {
        final int at = a.name().lastIndexOf(", bank ");
        return at < 0 || b.name().endsWith(a.name().substring(at));
    }

    /** Where a link's roller is, placed by {@code m}. */
    private Vec3 roller(final Mat4 m) {
        return new Vec3(m.at(0, 3), m.at(1, 3), m.at(2, 3));
    }
}

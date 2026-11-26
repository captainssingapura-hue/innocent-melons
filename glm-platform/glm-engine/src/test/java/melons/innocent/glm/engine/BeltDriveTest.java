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
 * The twin's final drive: its belt as long as its 178 teeth, round the output pulley of 34 and the rear pulley of 80,
 * the back axle behind the countershaft as far as that puts it and as high as its tyre is round; on the bike's right,
 * at the crankcase's back end, the clutch and the cam chains at its front end, the bike's left. As the bike rolls
 * forward, toward +x, the rear pulley turns with the wheel, the output pulley 80 to 34 times as fast the same way, and
 * the belt runs with them, a tooth a pitch on where the next was; and the belt and its pulleys clear of everything in
 * the engine, covered, but each other and the countershaft's bearing.
 */
class BeltDriveTest {

    private final EngineFigures v2 = new EngineRange().v2();
    private final BeltFigures belt = new BeltFigures(v2);

    @Test
    void asLongAsItsTeethTheBackAxleWhereThatPutsIt() {
        assertEquals(BeltFigures.TEETH * BeltFigures.PITCH, belt.belt().length(), 1e-6);
        final Spot shaft = belt.countershaft();
        final Spot axle = belt.axle();
        final double apart = Math.hypot(axle.u() - shaft.u(), axle.v() - shaft.v());
        assertTrue(apart > 80 && apart < 88, "some 84 cm behind the countershaft, not " + apart);
        assertTrue(axle.u() < shaft.u(), "behind it, toward -x");
        assertEquals(BeltFigures.REAR / (double) BeltFigures.FRONT, belt.rear() / belt.front(), 0.005);
        assertTrue(belt.plane() > v2.length(), "on the bike's right, past the crankcase's back end");
        assertTrue(new ClutchCover(v2).face() < 0 && new TimingCover(v2).face() < 0,
                "the clutch and the cam chains at its front end, the bike's left");
    }

    @Test
    void asTheBikeRollsThePulleysTurnAndTheBeltRunsWithThem() {
        final EngineInternals inside = new EngineInternals(v2);
        final List<Part> parts = new Assembly(inside.platform()).assembled().placed().keySet().stream()
                .map(p -> p.part()).toList();
        final Part rear = named(parts, "rear pulley");
        final Part output = named(parts, "output pulley");
        final Part chain = named(parts, "drive belt");
        assertTrue(inside.drives().wrapped(chain) && inside.drives().rolling(rear));
        final double tooth = belt.belt().length() / BeltFigures.TEETH;
        final double rolled = tooth / belt.rear() * BeltFigures.TYRE;
        final List<Mat4> rest = inside.drives().links(chain, 0, 0);
        final List<Mat4> on = inside.drives().links(chain, 0, rolled);
        assertEquals(BeltFigures.TEETH, rest.size());
        for (int i = 1; i < rest.size(); i++) {
            assertEquals(0, at(on.get(i)).minus(at(rest.get(i - 1))).length(), 1e-6,
                    "a tooth on where the one before was");
        }
        final Vec3 top = new Vec3(belt.axle().u(), belt.axle().v() + belt.rear(), belt.plane());
        assertTrue(at(inside.moved(rear, 0, 10), top).x() > top.x(),
                "the rear pulley's top going forward, toward +x, as the bike rolls");
        final Vec3 high = new Vec3(belt.countershaft().u(), belt.countershaft().v() + belt.front(), belt.plane());
        final Vec3 gone = at(inside.moved(output, 0, 1), high);
        final double turned = Math.atan2(gone.x() - belt.countershaft().u(), gone.y() - belt.countershaft().v());
        assertEquals(belt.wheel(1) * BeltFigures.REAR / BeltFigures.FRONT, turned, 1e-9, "80 to 34 as fast");
    }

    @Test
    void clearOfEverythingButEachOtherAndItsBearing() {
        final Assembled built = new Assembly(new EngineInternals(v2).platform(true)).assembled();
        final List<Part> parts = built.placed().keySet().stream().map(p -> p.part()).toList();
        final List<Part> drive = parts.stream().filter(p -> p.name().contains("pulley") || p.name().contains("belt"))
                .toList();
        assertEquals(3, drive.size());
        final TurnedTouching touching = new TurnedTouching();
        final List<String> touched = new ArrayList<>();
        for (final Part a : drive) {
            for (final Part b : parts) {
                if (!drive.contains(b) && !b.name().endsWith("crankcase")
                        && !touching.wheres(a.form(), new Mat4(), b.form(), new Mat4()).isEmpty()) {
                    touched.add(a.name() + " on " + b.name());
                }
            }
        }
        assertEquals(List.of(), touched);
    }

    private Part named(final List<Part> parts, final String called) {
        return parts.stream().filter(p -> p.name().contains(called)).findFirst().orElseThrow();
    }

    private Vec3 at(final Mat4 m) {
        return new Vec3(m.at(0, 3), m.at(1, 3), m.at(2, 3));
    }

    private Vec3 at(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }
}

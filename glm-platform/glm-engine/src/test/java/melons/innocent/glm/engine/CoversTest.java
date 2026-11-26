package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engines covered: each put together with a cover on each head, its coils, the timing cover and the trim cover,
 * a twin its cam covers and coils only, all made where they go; each head cover clear over its cams' lobes at their
 * highest and its caps' bolts; the timing cover's face ahead of every camshaft's wheel; and the trim cover over all
 * the rest, its coils and every head cover.
 */
class CoversTest {

    private final EngineRange range = new EngineRange();

    @Test
    void eachPutTogetherCoveredAllMadeWhereTheyGo() {
        for (final EngineFigures e : range.all()) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled open = new Assembly(inside.platform()).assembled();
            final Assembled covered = new Assembly(inside.platform(true)).assembled();
            assertEquals(List.of(), covered.problems(1e-6), e.name());
            final int trim = e.boxer() || e.twin() ? 0 : 1;
            final int timing = e.twin() ? 0 : 1;
            assertEquals(open.placed().size() + 2 * e.banks() + timing + trim, covered.placed().size(), e.name());
            covered.placed().keySet().forEach(p -> assertEquals(0, covered.at(p).length(), 1e-9, p.part().name()));
            final long plastic = covered.placed().keySet().stream().filter(p -> inside.cover(p.part()))
                    .filter(p -> p.part().made().material() == Material.PLASTIC).count();
            assertEquals(e.banks() + trim, plastic, e.name() + " its coils and its trim cover moulded in plastic");
        }
    }

    @Test
    void eachHeadCoverClearOverItsLobesAndBolts() {
        for (final EngineFigures e : range.all()) {
            final HeadFigures head = new HeadFigures(e);
            final double under = new HeadCover(e, 0).top() - 0.2;
            final double lobes = head.high() + new Camshaft(e, 0, true).base() + new ValveTiming(e).most();
            final double bolts = head.high() + head.journal() + 0.8 + 0.6;
            assertTrue(under > lobes + 0.5 && under > bolts + 0.5, e.name() + " " + under);
        }
    }

    @Test
    void theTimingCoversFaceAheadOfEveryWheel() {
        for (final EngineFigures e : range.all()) {
            final HeadFigures head = new HeadFigures(e);
            for (int b = 0; b < e.banks(); b++) {
                assertTrue(head.front(b) - 2.5 > new TimingCover(e).face() + 0.2, e.name() + " bank " + b);
            }
        }
    }

    @Test
    void theTrimCoverOverAllTheRest() {
        for (final EngineFigures e : range.all().stream().filter(e -> !e.boxer()).toList()) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled covered = new Assembly(inside.platform(true)).assembled();
            double highest = -Double.MAX_VALUE;
            for (final var p : covered.placed().keySet()) {
                final Part part = p.part();
                if (!part.name().endsWith("trim cover")) {
                    highest = Math.max(highest, part.form().points().stream().mapToDouble(Vec3::y).max()
                            .orElseThrow());
                }
            }
            final double under = new TrimCover(e).reach()[4] - 0.2;
            assertTrue(under > highest + 0.5, e.name() + " " + under + " over " + highest);
        }
    }
}

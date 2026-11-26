package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.engine.EngineRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's bonnet and bumpers: the bonnet hinged to the bulkhead, the V6 under it with room to spare and
 * touching it nowhere; the bumpers bolted to the shell, black plastic, each with its chrome strip, the front one's face
 * ahead of the nose and the rear one's behind the tail, both clear of the tyres.
 */
class BonnetAndBumpersTest {

    private final SantanaLines lines = new SantanaLines();
    private final Platform platform = new Santana().platform(new EngineBay(new EngineRange().all().stream()
            .filter(e -> e.name().equals("V6")).findFirst().orElseThrow(), true)).platform();
    private final Assembled built = new Assembly(platform).assembled();
    private final Touching touching = new Touching();

    @Test
    void theBonnetHingedToTheBulkheadTheV6UnderIt() {
        assertEquals(List.of(), built.problems(1e-6));
        assertTrue(platform.connections().stream().anyMatch(c -> c.carried().name().equals("bonnet")
                && c.carrier().name().equals("bulkhead") && c.joining() == Joining.HINGE));
        final PartAt bonnet = part("bonnet", Side.MIDDLE);
        final Bonnet shape = new Bonnet(lines);
        for (final PartAt p : built.placed().keySet()) {
            if (p.part().group() == Group.ENGINE || p.part().group() == Group.COVERS) {
                for (final Vec3 v : p.shape().form().points()) {
                    final Vec3 q = v.plus(built.at(p));
                    assertTrue(q.y() < shape.height(q.x(), q.z()) - 1, p.said() + " under the bonnet at " + q);
                }
                assertFalse(touching.touch(p.shape().form(), built.at(p), bonnet.shape().form(), built.at(bonnet)),
                        p.said() + " meets the bonnet");
            }
        }
    }

    @Test
    void theBumpersBoltedOnBlackPlasticEachWithItsChromeStrip() {
        final double k = lines.scale();
        for (final String name : List.of("front bumper", "rear bumper")) {
            final PartAt bumper = part(name, Side.MIDDLE);
            assertEquals(Material.PLASTIC, bumper.part().made().material());
            assertTrue(bumper.part().made().bare(), "black plastic, bare");
            assertEquals(Group.TRIM, bumper.part().group());
            assertEquals(Material.STEEL, part(name + " strip", Side.MIDDLE).part().made().material());
            assertTrue(platform.connections().stream().anyMatch(c -> c.carried().name().equals(name)
                    && c.carrier().name().equals("underbody") && c.joining() == Joining.BOLTED));
        }
        assertEquals(928 * k / 10, extent(part("front bumper", Side.MIDDLE), true), 0.1, "its face ahead");
        assertEquals(-3_554 * k / 10, extent(part("rear bumper", Side.MIDDLE), false), 0.1, "its face behind");
        assertTrue(extent(part("front bumper", Side.MIDDLE), true) > extent(part("front wing", Side.RIGHT), true));
    }

    @Test
    void theBumpersClearOfTheTyres() {
        for (final PartAt tyre : built.placed().keySet()) {
            if (tyre.part().group() == Group.TYRES) {
                for (final String name : List.of("front bumper", "rear bumper")) {
                    final PartAt bumper = part(name, Side.MIDDLE);
                    assertFalse(touching.touch(tyre.shape().form(), built.at(tyre), bumper.shape().form(),
                            built.at(bumper)), tyre.said() + " meets the " + name);
                }
            }
        }
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }

    /** How far ahead {@code part} reaches, or behind if not {@code ahead}. */
    private double extent(final PartAt part, final boolean ahead) {
        final Vec3 at = built.at(part);
        final var along = part.shape().form().points().stream().mapToDouble(v -> v.plus(at).z());
        return ahead ? along.max().orElseThrow() : along.min().orElseThrow();
    }
}

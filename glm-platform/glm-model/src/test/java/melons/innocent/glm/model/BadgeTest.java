package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's badges: the maker's roundel on the grille and on the tail panel under the number plate, each a
 * painted disc and, standing proud of it, its ring and letters in a bare metal, so another may be put in its place;
 * each as round as it is, its letters even each side; touching nothing but what holds it.
 */
class BadgeTest {

    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Touching touching = new Touching();

    @Test
    void onTheGrilleAndTheTailEachADiscAndItsReliefInABareMetal() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final String name : List.of("front badge", "rear badge")) {
            final PartAt relief = part(name);
            final PartAt disc = part(name + " disc");
            assertEquals(Group.BADGES, relief.part().group());
            assertEquals(Group.BADGES, disc.part().group());
            assertTrue(relief.part().made().bare() && relief.part().made().material().metal(), "a bare metal");
            assertTrue(!disc.part().made().bare(), "the disc painted");
        }
        assertTrue(middle(part("front badge")).z() > 80, "the front one ahead, on the grille");
        assertTrue(middle(part("rear badge")).z() < -360, "the rear one behind, on the tail");
    }

    @Test
    void asRoundAsItIsItsLettersEvenEachSide() {
        final Roundel roundel = new Roundel(5);
        final List<Vec3> points = roundel.relief().points();
        assertTrue(points.stream().allMatch(p -> Math.hypot(p.x(), p.y()) <= 5 + 1e-9), "no wider than it is round");
        final double left = points.stream().mapToDouble(Vec3::x).min().orElseThrow();
        final double right = points.stream().mapToDouble(Vec3::x).max().orElseThrow();
        assertEquals(-left, right, 1e-6, "even each side");
        assertTrue(points.stream().allMatch(p -> p.z() >= -1e-9), "all of it proud of the disc's face");
    }

    @Test
    void touchingNothingButWhatHoldsIt() {
        for (final String name : List.of("front badge", "rear badge")) {
            final PartAt relief = part(name);
            for (final PartAt q : built.placed().keySet()) {
                final String n = q.part().name();
                if (!n.startsWith(name) && !n.equals("grille panel") && !n.equals("tail panel")) {
                    assertFalse(touching.touch(relief.shape().form(), built.at(relief), q.shape().form(),
                            built.at(q)), name + " meets " + q.said());
                }
            }
        }
    }

    private PartAt part(final String name) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == Side.MIDDLE)
                .findFirst().orElseThrow();
    }

    private Vec3 middle(final PartAt part) {
        final Vec3 at = built.at(part);
        final List<Vec3> points = part.shape().form().points();
        return points.stream().map(p -> p.plus(at)).reduce(new Vec3(0, 0, 0), Vec3::plus).times(1.0 / points.size());
    }
}

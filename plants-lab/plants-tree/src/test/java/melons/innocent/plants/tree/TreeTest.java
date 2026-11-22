package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tree with no leaves, grown from a habit and a number. The same habit and number are the same tree, and another
 * number another tree. Its trunk stands on the ground and every other bough begins where the bough it forks from
 * ends. No bough thickens as it runs, and a fork's two boughs are through together what the bough before them was
 * through. It ends in twigs, hundreds of them. An oak is as high and as wide as an oak. Its wood is a mesh with
 * every face turned out from the bough it is on, and nothing under the ground. And no tree grows by figures that
 * make no sense.
 */
class TreeTest {

    private final Tree oak = new Tree();
    private final List<Bough> boughs = oak.boughs();

    @Test
    void theSameHabitAndNumberAreTheSameTreeAndAnotherNumberAnother() {
        assertEquals(boughs, new Tree().boughs());
        final List<Bough> other = new Tree(new Habit(), 2).boughs();
        assertNotEquals(boughs, other);
        assertEquals(boughs.getFirst().begins(), other.getFirst().begins(), "both stand at their frame's middle");
    }

    @Test
    void theTrunkStandsOnTheGroundAndEveryOtherBoughBeginsWhereTheOneItForksFromEnds() {
        final Bough trunk = boughs.getFirst();
        assertEquals(new Vec3(0, 0, 0), trunk.begins());
        assertEquals(-1, trunk.from());
        assertEquals(0, trunk.forks());
        assertTrue(trunk.ends().y() > 280 && trunk.length() < 340, "it runs up some three metres: " + trunk.ends());
        for (int bough = 1; bough < boughs.size(); bough++) {
            final Bough from = boughs.get(boughs.get(bough).from());
            assertTrue(boughs.get(bough).from() < bough, "after the bough it forks from");
            assertEquals(from.ends(), boughs.get(bough).begins());
            assertEquals(from.forks() + 1, boughs.get(bough).forks());
        }
    }

    @Test
    void noBoughThickensAsItRunsAndAForksTwoAreThroughTogetherWhatTheBoughBeforeThemWas() {
        for (final Bough bough : boughs) {
            for (int point = 1; point < bough.thick().size(); point++) {
                assertTrue(bough.thick().get(point) <= bough.thick().get(point - 1) + 1e-12);
            }
        }
        for (int bough = 0; bough < boughs.size(); bough++) {
            final int here = bough;
            final List<Bough> forked = boughs.stream().filter(one -> one.from() == here).toList();
            if (!forked.isEmpty()) {
                final double before = Math.pow(boughs.get(bough).thick().getLast(), 2);
                assertEquals(2, forked.size(), "a bough forks in two");
                assertEquals(before, forked.stream().mapToDouble(one -> Math.pow(one.thick().getFirst(), 2)).sum(),
                        1e-9 * before, "through together what it was through");
            }
        }
    }

    @Test
    void itEndsInHundredsOfTwigsAndIsAsHighAndAsWideAsAnOak() {
        final long twigs = boughs.stream().filter(bough -> bough.thick().getLast() < 0.35).count();
        assertTrue(twigs > 300 && twigs < 20_000, "twigs: " + twigs);
        assertEquals(boughs.size(), 2 * twigs - 1, "every bough forks in two or is a twig");
        assertTrue(oak.height() > 900 && oak.height() < 2500, "how high: " + oak.height());
        assertTrue(oak.reach() > 400 && oak.reach() < 1800, "how far it reaches: " + oak.reach());
        assertTrue(boughs.stream().flatMap(bough -> bough.points().stream()).allMatch(point -> point.y() >= 0),
            "nothing of it under the ground");
    }

    @Test
    void itsWoodIsAMeshWithEveryFaceTurnedOutFromTheBoughItIsOn() {
        final Wooded wood = new Wooded(oak);
        final Mesh mesh = wood.formed();
        assertTrue(mesh.triangles() > 10_000, "triangles: " + mesh.triangles());
        assertEquals(12, wood.sides(34));
        assertEquals(4, wood.sides(0.3));
        // the trunk alone: every face of it faces away from the trunk's own line
        final Mesh trunk = new Wooded(List.of(boughs.getFirst())).formed();
        for (int triangle = 0; triangle < trunk.triangles(); triangle++) {
            final Vec3 centre = trunk.corner(triangle, 0).plus(trunk.corner(triangle, 1))
                    .plus(trunk.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(trunk.crossed(triangle).dot(centre.minus(new Vec3(0, 150, 0))) > 0, "triangle " + triangle);
        }
    }

    @Test
    void noTreeGrowsByFiguresThatMakeNoSense() {
        assertThrows(IllegalArgumentException.class, () -> new Habit(320, 34, 1.5, 0.9, 1.2, 34, 0.1, 0.05, 0.35));
        assertThrows(IllegalArgumentException.class, () -> new Habit(320, 0.2, 1.5, 0.9, 0.8, 34, 0.1, 0.05, 0.35));
        assertThrows(IllegalArgumentException.class, () -> new Habit(0, 34, 1.5, 0.9, 0.8, 34, 0.1, 0.05, 0.35));
    }
}

package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A wood cut into patches, each drawn far off as one stand. A wood of trees seven metres apart, 25 m patches: every
 * patch of enough trees is a stand, no tree is in two, and a patch of too few trees is none. A stand is exactly its
 * patch, its trees within it, from their feet as high as they stand, and reaches round its middle as far as their
 * crowns may.
 */
class PatchedTest {

    private final Patched patched = new Patched(2_500, 4);

    @Test
    void everyPatchOfEnoughTreesIsAStandExactlyItsPatchAndNoTreeIsInTwo() {
        final List<Patched.Rooted> trees = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                trees.add(new Patched.Rooted(new Vec3(i * 700 + 350, 40, j * 700 + 350), 1_500));
            }
        }
        trees.add(new Patched.Rooted(new Vec3(-10_000, 0, -10_000), 1_500));
        final List<Stand> stands = patched.stands(trees);
        final Set<Integer> seen = new HashSet<>();
        stands.forEach(stand -> stand.trees().forEach(tree -> assertTrue(seen.add(tree), "a tree in two stands")));
        assertEquals(trees.size() - 1, seen.size(), "every tree but the lone one is in a stand");
        for (final Stand stand : stands) {
            assertTrue(stand.trees().size() >= 4, "each of enough trees");
            assertEquals(0, stand.corner().x() % 2_500, 1e-9, "on the patches' lines");
            assertEquals(0, stand.corner().z() % 2_500, 1e-9);
            assertEquals(40, stand.corner().y(), 1e-9, "from its trees' feet");
            assertEquals(1_500, stand.tall(), 1e-9, "as high as they stand");
            for (final int tree : stand.trees()) {
                final Vec3 foot = trees.get(tree).foot();
                assertTrue(foot.x() >= stand.corner().x() && foot.x() < stand.corner().x() + 2_500
                        && foot.z() >= stand.corner().z() && foot.z() < stand.corner().z() + 2_500, "within it");
                assertTrue(foot.plus(new Vec3(1_250, 1_500, 1_250)).minus(stand.middle()).length() <= stand.reach(),
                        "a crown half a patch round its foot is within its reach");
            }
        }
    }
}

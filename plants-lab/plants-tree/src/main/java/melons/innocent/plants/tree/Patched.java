package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A wood cut into square patches {@code side} across, each drawn far off as one stand ({@link Stand}): its trees
 * taken together, so that kilometres of wood are chosen patch by patch and not tree by tree. A patch of fewer than
 * {@code fewest} trees is no stand, and its trees are chosen one by one however far off. Worked out from the trees as
 * they stand, never written beside them. Centimetres.
 *
 * @param side   how far across a patch is
 * @param fewest the fewest trees a patch must have to be drawn as a stand
 */
public record Patched(double side, int fewest) {

    /** A tree of the wood: where its foot is, and how tall it stands. */
    public record Rooted(Vec3 foot, double tall) {
    }

    /** The stands of {@code trees}, patch by patch, in order across and then along; patches too few in trees, none. */
    public List<Stand> stands(final List<Rooted> trees) {
        final Map<Long, List<Integer>> patches = new TreeMap<>();
        for (int tree = 0; tree < trees.size(); tree++) {
            final Vec3 foot = trees.get(tree).foot();
            final long across = (long) Math.floor(foot.x() / side);
            final long along = (long) Math.floor(foot.z() / side);
            patches.computeIfAbsent(across << 32 ^ (along & 0xffffffffL), key -> new ArrayList<>()).add(tree);
        }
        final List<Stand> stands = new ArrayList<>();
        for (final List<Integer> patch : patches.values()) {
            if (patch.size() < fewest) {
                continue;
            }
            double low = 0;
            double high = 0;
            for (final int tree : patch) {
                low += trees.get(tree).foot().y();
                high += trees.get(tree).tall();
            }
            final Vec3 first = trees.get(patch.getFirst()).foot();
            final Vec3 corner = new Vec3(Math.floor(first.x() / side) * side, low / patch.size(),
                    Math.floor(first.z() / side) * side);
            stands.add(new Stand(corner, side, high / patch.size(), patch));
        }
        return stands;
    }
}

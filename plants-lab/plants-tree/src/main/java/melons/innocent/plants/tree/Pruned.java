package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tree as a stage draws it, worked out from the tree as it is grown and never written beside it: the boughs the
 * stage keeps, as wood; and the boughs it drops, gathered into clumps, each the wood that leaves one of the boughs
 * thick enough to gather to ({@link Stage#gathers}), and drawn as a haze that hides as much as that wood would.
 * Every bough is either kept or in exactly one clump; a stage that drops nothing has no clumps.
 */
public record Pruned(List<Bough> boughs, Stage stage) {

    /** How many points round a clump, and how many rings from end to end. */
    private static final int ROUND = 8;
    private static final int RINGS = 4;

    public Pruned {
        boughs = List.copyOf(boughs);
    }

    /** {@code tree} at {@code stage}. */
    public Pruned(final Tree tree, final Stage stage) {
        this(tree.boughs(), stage);
    }

    /** The boughs kept, as wood, the trunk first. */
    public List<Bough> kept() {
        return boughs.stream().filter(stage::keeps).toList();
    }

    /** The boughs dropped, in the order grown. */
    public List<Bough> dropped() {
        return boughs.stream().filter(bough -> !stage.keeps(bough)).toList();
    }

    /** The kept wood as one mesh, its boughs rounded in as many fewer sides as the stage says. */
    public Mesh wood() {
        return new Wooded(kept(), stage.fewer()).formed();
    }

    /**
     * The dropped wood, gathered: each dropped bough goes to the nearest bough it grows from, through the kept
     * ones, that is thick enough to gather to, or else to the trunk; each bough so gathered to has one clump.
     */
    public List<Clump> clumps() {
        final Map<Integer, List<Bough>> gathered = new LinkedHashMap<>();
        for (int bough = 0; bough < boughs.size(); bough++) {
            if (!stage.keeps(boughs.get(bough))) {
                gathered.computeIfAbsent(gatheredTo(bough), to -> new ArrayList<>()).add(boughs.get(bough));
            }
        }
        return gathered.values().stream().map(Clump::new).toList();
    }

    /** The bough the dropped bough {@code dropped} is gathered to, by its place among the tree's boughs. */
    private int gatheredTo(final int dropped) {
        int at = boughs.get(dropped).from();
        while (at > 0 && !(stage.keeps(boughs.get(at)) && boughs.get(at).thick().getFirst() >= stage.gathers())) {
            at = boughs.get(at).from();
        }
        return Math.max(at, 0);
    }

    /**
     * The clumps as meshes, by how much they hide: each clump drawn at whichever of {@code levels} is nearest what
     * it hides, by how many times over, not by how much more (so a clump that hides a hundredth is not drawn as one
     * that hides a tenth), and the clumps at one level one mesh, keyed by the level. A level with no clump has none.
     */
    public Map<Double, Mesh> hazes(final List<Double> levels) {
        final Map<Double, Forming> by = new LinkedHashMap<>();
        for (final Clump clump : clumps()) {
            double nearest = levels.getFirst();
            for (final double level : levels) {
                if (Math.abs(Math.log(level / clump.hides())) < Math.abs(Math.log(nearest / clump.hides()))) {
                    nearest = level;
                }
            }
            clump.formed(by.computeIfAbsent(nearest, l -> new Forming(new Vec3(0, 0, 0))), ROUND, RINGS);
        }
        final Map<Double, Mesh> meshes = new LinkedHashMap<>();
        by.forEach((level, forming) -> meshes.put(level, forming.formed()));
        return meshes;
    }
}

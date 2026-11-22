package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A land feature, lying on the ground: water, what grows, what is bare, what is farmed. Each is labelled, is a kind
 * of what exists (a leaf of the taxonomy: a river, a bay, a meadow, a forest, a field), and has a colour of its own
 * to be shown in; where it lies is worked out from the ground and the map's lines, the same every time. Features are
 * laid one over another, the later over the earlier, so water lies over the meadow it floods.
 */
public sealed interface Feature extends Scalar permits Band, Steep, Area, Sea, Watercourse {

    /** What it is labelled. */
    String label();

    /** The kind of thing it is. */
    Leaf<?> kind();

    /** The colour it is shown in. */
    Hue hue();

    /** Where it lies, on {@code terrain}'s ground and along its map's lines. */
    Coverage coverage(Terrain terrain);

    /**
     * Where a feature lies: whether it lies at a spot, the ground there as already worked out, so nothing asks the
     * ground again.
     */
    @FunctionalInterface
    interface Coverage {

        /** Whether it lies at {@code at}. */
        boolean covers(Sample at);
    }
}

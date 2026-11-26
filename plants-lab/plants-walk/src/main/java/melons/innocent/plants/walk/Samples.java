package melons.innocent.plants.walk;

import java.util.List;

/**
 * A set of samples of forest: so many squares of trees, each worked out once from its number alone, the same every
 * time, and each different from the others. A forest is then made of these and nothing else, each standing many
 * times: no two trees of a forest need differ for it to read as a forest, only the trees in sight at once, so a set
 * large enough that the same sample seldom shows twice in a view is as good to the eye as a forest of which every
 * square is its own, and its meshes are made once and kept, however far the forest goes.
 *
 * <p>A sample is settled at its finest, tree by tree: which grown tree stands where, turned and sized how. Whatever
 * coarser is drawn of it is worked out from that.
 *
 * @param count  how many samples there are
 * @param side   how long a sample's side is, in centimetres
 * @param grown  how many grown trees there are to choose among
 * @param chance the number the set is worked out from
 */
public record Samples(int count, double side, int grown, long chance) {

    private static final long MIXED = 7_919L;

    public Samples {
        if (count < 1 || !(side > 0) || grown < 1) {
            throw new IllegalArgumentException("a set of samples has at least one sample, of some size, of at least"
                    + " one grown tree: " + count + " of " + side + " cm, of " + grown);
        }
    }

    /** The trees of the sample numbered {@code sample}, in its own frame: its middle where nothing is. */
    public List<WoodDrawn.Planted> planted(final int sample) {
        if (sample < 0 || sample >= count) {
            throw new IllegalArgumentException("there are " + count + " samples, and none numbered " + sample);
        }
        return new Forested(side, side, grown, chance * MIXED + sample).planted(0);
    }
}

package melons.innocent.plants.tree;

/**
 * How a kind of tree grows: the figures its growing goes by. A tree of this habit is grown bough by bough from its
 * trunk, each bough running some way, thinning as it goes, and then forking into two, until what is left is a twig.
 * Lengths are in centimetres and angles in degrees.
 *
 * @param trunkTall  how far the trunk runs before it first forks
 * @param trunkThick how thick the trunk is, from its middle to its bark
 * @param flare      how many times as thick the trunk is at the ground, where it spreads into its roots
 * @param taper      how thick a bough is at its far end, of what it was at its near end
 * @param shorter    how long a bough is, of the bough it forks from
 * @param spread     how far apart the two boughs of a fork turn from the way the bough before them was going
 * @param wander     how far a bough strays from straight as it runs, from nothing for dead straight
 * @param rising     how much a bough is drawn upward toward the light as it runs
 * @param twig       how thin a bough is when it stops forking: the thickness of a twig
 * @param lead       how much of a bough the thicker of a fork's two takes, at most, from a half for two alike to
 *                   nearly all for a tree whose one leader runs on up through its crown with the other going aside
 * @param droop      how much a slender bough is drawn down under its own weight as it runs: nothing for a tree
 *                   whose twigs stand up, more for one whose twigs hang
 * @param step       for a tree with a leader, how far the leader runs between one side bough and the next, the
 *                   side boughs then as long as their thickness gives them; nothing, for a tree with no leader,
 *                   whose boughs are each as long as the bough they fork from gives them
 */
public record Habit(double trunkTall, double trunkThick, double flare, double taper, double shorter, double spread,
                    double wander, double rising, double twig, double lead, double droop, double step) {

    public Habit {
        if (!(trunkTall > 0 && trunkThick > twig && twig > 0 && flare >= 1 && taper > 0 && taper <= 1
                && shorter > 0 && shorter < 1 && spread > 0 && spread < 90 && wander >= 0 && rising >= 0
                && lead >= 0.5 && lead < 1 && droop >= 0 && step >= 0)) {
            throw new IllegalArgumentException("no tree grows so: " + this);
        }
    }

    /** A tree of these figures whose forks share out as an oak's do, and whose twigs do not hang. */
    public Habit(final double trunkTall, final double trunkThick, final double flare, final double taper,
                 final double shorter, final double spread, final double wander, final double rising,
                 final double twig) {
        this(trunkTall, trunkThick, flare, taper, shorter, spread, wander, rising, twig, 0.78, 0, 0);
    }

    /**
     * An oak grown in the open, as it stands in winter: a short thick trunk, forking low and wide into heavy boughs
     * that wander, and on into a broad crown of fine twigs. The figures are their author's, from no measured tree.
     */
    public Habit() {
        this(320, 34, 1.5, 0.95, 0.78, 38, 0.14, 0.05, 0.35);
    }
}

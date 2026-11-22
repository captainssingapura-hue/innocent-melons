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
 */
public record Habit(double trunkTall, double trunkThick, double flare, double taper, double shorter, double spread,
                    double wander, double rising, double twig) {

    public Habit {
        if (!(trunkTall > 0 && trunkThick > twig && twig > 0 && flare >= 1 && taper > 0 && taper <= 1
                && shorter > 0 && shorter < 1 && spread > 0 && spread < 90 && wander >= 0 && rising >= 0)) {
            throw new IllegalArgumentException("no tree grows so: " + this);
        }
    }

    /**
     * An oak grown in the open, as it stands in winter: a short thick trunk, forking low and wide into heavy boughs
     * that wander, and on into a broad crown of fine twigs. The figures are their author's, from no measured tree.
     */
    public Habit() {
        this(320, 34, 1.5, 0.95, 0.78, 38, 0.14, 0.05, 0.35);
    }
}

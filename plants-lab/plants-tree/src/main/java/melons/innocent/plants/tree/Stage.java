package melons.innocent.plants.tree;

/**
 * How finely a tree is drawn, from as it is grown to a trunk in a haze: each stage keeps the boughs at least so
 * thick and drops the rest, and the wood it drops is drawn instead as clumps that enclose it, seen through as much
 * as the wood in them lets through ({@link Pruned}). Each stage is for where the thinnest bough it keeps is still a
 * pixel across or more ({@link Staging}), so what it drops is what could not be seen one by one.
 *
 * <p>Each stage's cut is four times the one before; the cuts, and which bough a clump gathers to, are their
 * author's, set by eye and by how many triangles each stage leaves.
 */
public enum Stage {

    /** Every bough, as grown. */
    FINE(0, 0, 1),
    /** The twigs gone: boughs half a centimetre thick or more, from the middle; a clump for each bough of 2 or more. */
    TWIGS_GONE(0.5, 2, 1),
    /** Boughs of 2 cm or more; a clump for each bough of 8 or more; the wood kept rounded in half as many sides. */
    BOUGHS(2, 8, 2),
    /** The limbs, 8 cm or more; a clump for each of them. */
    LIMBS(8, 8, 2),
    /** The trunk, and whatever else is 16 cm or more; all the rest one clump, the crown. */
    WHOLE(16, Double.MAX_VALUE, 2);

    private final double least;
    private final double gathers;
    private final int fewer;

    Stage(final double least, final double gathers, final int fewer) {
        this.least = least;
        this.gathers = gathers;
        this.fewer = fewer;
    }

    /** How thick a bough is at least, from its middle to its bark where it begins, to be kept, in centimetres. */
    public double least() {
        return least;
    }

    /** How thick the bough is that the dropped wood beyond it is gathered to, as one clump. */
    public double gathers() {
        return gathers;
    }

    /** How many times fewer sides the kept wood is rounded in than as grown. */
    public int fewer() {
        return fewer;
    }

    /** Whether {@code bough} is kept at this stage. */
    public boolean keeps(final Bough bough) {
        return bough.forks() == 0 || bough.thick().getFirst() >= least;
    }

    /** The next coarser stage; the coarsest, itself. */
    public Stage coarser() {
        return values()[Math.min(ordinal() + 1, values().length - 1)];
    }
}

package melons.innocent.plants.tree;

/**
 * The kinds of tree there are, each with the habit it grows by, as it stands in winter with no leaves. Each habit's
 * figures are their author's, from no measured tree; they were set by eye against what each kind is known for.
 */
public enum Kind {

    /** An oak grown in the open: a short thick trunk, forking low and wide into heavy boughs, a broad crown. */
    OAK("Oak", new Habit()),

    /**
     * A walnut (核桃, Juglans regia) grown in the open: a short stout trunk, forking low into a few heavy boughs
     * that spread wide into a broad rounded crown, and twigs that end thick and blunt, far stouter than an oak's.
     */
    WALNUT("Walnut", new Habit(270, 38, 1.4, 0.94, 0.8, 40, 0.08, 0.075, 0.75, 0.74, 0, 0)),

    /**
     * A white birch (白桦, Betula platyphylla): a tall slender trunk with one leader running on up through a narrow
     * crown, side boughs climbing steeply from it, and a great many fine twigs that hang at their ends.
     */
    BIRCH("White birch", new Habit(380, 15, 1.25, 0.92, 0.67, 26, 0.07, 0.06, 0.13, 0.9, 0.32, 70));

    private final String said;
    private final Habit habit;

    Kind(final String said, final Habit habit) {
        this.said = said;
        this.habit = habit;
    }

    /** Its name, in words. */
    public String said() {
        return said;
    }

    /** The habit it grows by. */
    public Habit habit() {
        return habit;
    }

    /** A tree of this kind, the {@code number}th: the same number is the same tree. */
    public Tree tree(final long number) {
        return new Tree(habit, number);
    }
}

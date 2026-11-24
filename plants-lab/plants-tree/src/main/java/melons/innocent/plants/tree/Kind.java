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
    BIRCH("White birch", new Habit(380, 15, 1.25, 0.92, 0.67, 26, 0.07, 0.06, 0.13, 0.9, 0.32, 70)),
    /**
     * A Lombardy poplar (钻天杨, Populus nigra 'Italica'): far the tallest, a column: one leader running straight up,
     * and side boughs leaving it at a narrow angle and climbing close beside it, so the whole is many times as high
     * as it is wide.
     */
    POPLAR("Lombardy poplar", new Habit(260, 34, 1.3, 0.93, 0.72, 18, 0.05, 0.16, 0.25, 0.95, 0, 60)),
    /**
     * A weeping willow (垂柳, Salix babylonica): a short thick trunk forking low into wide boughs, and from them a
     * great many long slender twigs that hang straight down, golden where they are thin.
     */
    WILLOW("Weeping willow", new Habit(260, 36, 1.45, 0.95, 0.8, 42, 0.12, 0.04, 0.32, 0.76, 0.75, 0)),
    /**
     * A plane (悬铃木, Platanus): tall and broad, a heavy straight trunk carrying a high wide crown of stout boughs,
     * its bark in patches of cream, olive and brown.
     */
    PLANE("Plane", new Habit(340, 40, 1.4, 0.95, 0.8, 34, 0.1, 0.07, 0.55, 0.8, 0, 0));

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

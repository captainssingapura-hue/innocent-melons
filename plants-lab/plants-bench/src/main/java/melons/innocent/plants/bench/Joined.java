package melons.innocent.plants.bench;

/**
 * How the trees on show are drawn: each a copy of its grown tree at its stage, in a few draws for any number, as a
 * wood draws them; or all as one mesh, every tree's finest wood merged ahead into one, as grown, as a far tile of a
 * wood might be baked. Both stand beside the same as grown alike, to be held against it by eye and by cost.
 */
public enum Joined {
    APART("Each tree on its own, as copies"),
    AS_ONE("All as one mesh, as grown");

    private final String title;

    Joined(final String title) {
        this.title = title;
    }

    /** What it is called in the bench's window. */
    public String title() {
        return title;
    }
}

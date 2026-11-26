package melons.innocent.plants.bench;

import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Which kinds of tree are on show: one kind, or all three mixed. The bench grows two trees of each kind
 * ({@link #grown}), and a tree on show is one of those, by its number among them.
 */
public enum Kinds {
    OAK("Oak", Kind.OAK),
    WALNUT("Walnut", Kind.WALNUT),
    BIRCH("White birch", Kind.BIRCH),
    MIXED("All three, mixed", null);

    /** How many trees of each kind the bench grows. */
    private static final int EACH = 2;
    /** How many kinds are mixed where they are: the oak, the walnut and the white birch. */
    private static final int THREE = 3;

    private final String title;
    private final Kind kind;

    Kinds(final String title, final Kind kind) {
        this.title = title;
        this.kind = kind;
    }

    /** What it is called in the bench's window. */
    public String title() {
        return title;
    }

    /** The trees the bench grows: two of each kind, numbers 1 and 2, the oaks first, then walnuts, then birches. */
    public List<WoodDrawn.Grown> grown() {
        final List<WoodDrawn.Grown> grown = new ArrayList<>();
        for (final Kind each : List.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH)) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(each, each.tree(number)));
            }
        }
        return grown;
    }

    /** The first tree grown of these kinds, by its number among {@link #grown}: for mixed, the first oak. */
    public int first() {
        return kind == null ? 0 : kind.ordinal() * EACH;
    }

    /** Some tree grown of these kinds, chosen by {@code chance}, by its number among {@link #grown}. */
    public int any(final Random chance) {
        final int of = kind == null ? chance.nextInt(THREE) : kind.ordinal();
        return of * EACH + chance.nextInt(EACH);
    }
}

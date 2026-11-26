package melons.innocent.plants.walk;

import melons.innocent.plants.tree.Stage;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * How far from the eye each level of an endless forest's samples reaches, in metres: within {@code fine} a sample
 * is drawn at its finest, every twig; then with its twigs gone, then its boughs, then its limbs, then its trunks
 * alone as far as {@code far}, beyond which nothing is drawn. Set one of them as far as the last and nothing
 * coarser than it is ever drawn: {@code fine} as far as {@code far} is the finest everywhere.
 *
 * @param fine   within this, the finest
 * @param twigs  within this, twigs gone
 * @param boughs within this, boughs
 * @param limbs  within this, limbs
 * @param far    within this, trunks alone; beyond it, nothing
 */
public record Reaches(double fine, double twigs, double boughs, double limbs, double far) {

    private static final double METRE = 100;

    public Reaches {
        if (!(fine >= 0 && twigs >= fine && boughs >= twigs && limbs >= boughs && far >= limbs)) {
            throw new IllegalArgumentException("each level reaches at least as far as the finer one before it: "
                    + fine + ", " + twigs + ", " + boughs + ", " + limbs + ", " + far);
        }
    }

    /**
     * As the run says: {@code -Dforest.fine=} (60), {@code -Dforest.twigs=} (120), {@code -Dforest.boughs=} (300),
     * {@code -Dforest.limbs=} (500) and {@code -Dforest.far=} (1200), each in metres. Unless said, then: the finest
     * within sixty metres, twigs gone to a hundred and twenty, boughs to three hundred, limbs to five hundred and trunks alone
     * to twelve hundred, as far as a trunk is to be made out at all, and nothing beyond.
     */
    public Reaches() {
        this(Double.parseDouble(System.getProperty("forest.fine", "60")),
                Double.parseDouble(System.getProperty("forest.twigs", "120")),
                Double.parseDouble(System.getProperty("forest.boughs", "300")),
                Double.parseDouble(System.getProperty("forest.limbs", "500")),
                Double.parseDouble(System.getProperty("forest.far", "1200")));
    }

    /** The levels in use: each that reaches further than the finer one before it. */
    public Set<Stage> levels() {
        final Set<Stage> levels = EnumSet.noneOf(Stage.class);
        final double[] reach = {0, fine, twigs, boughs, limbs, far};
        for (final Stage level : Stage.values()) {
            if (reach[level.ordinal() + 1] > reach[level.ordinal()]) {
                levels.add(level);
            }
        }
        return levels;
    }

    /**
     * The levels a forest has on the card before it is first drawn: trunks alone always, so no square is ever
     * missing; boughs where shadows are cast, since it is as its boughs that a square stands for rays; and every
     * level in use, where everything is baked {@code beforehand}.
     */
    public Set<Stage> first(final boolean beforehand, final boolean shadowed) {
        final Set<Stage> first = beforehand ? levels() : EnumSet.noneOf(Stage.class);
        first.add(Stage.WHOLE);
        if (shadowed) {
            first.add(Stage.BOUGHS);
        }
        return first;
    }

    /** The level a sample so many centimetres from the eye is drawn at; none, beyond the furthest. */
    public Optional<Stage> level(final double away) {
        final double metres = away / METRE;
        return metres > far ? Optional.empty() : Optional.of(metres <= fine ? Stage.FINE
                : metres <= twigs ? Stage.TWIGS_GONE : metres <= boughs ? Stage.BOUGHS
                : metres <= limbs ? Stage.LIMBS : Stage.WHOLE);
    }
}

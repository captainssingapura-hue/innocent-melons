package melons.innocent.plants.walk;

import melons.innocent.plants.tree.Stage;

import java.util.Optional;

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
     * As the run says: {@code -Dforest.fine=} (20), {@code -Dforest.twigs=} (60), {@code -Dforest.boughs=} (200),
     * {@code -Dforest.limbs=} (600) and {@code -Dforest.far=} (2000), each in metres.
     */
    public Reaches() {
        this(Double.parseDouble(System.getProperty("forest.fine", "20")),
                Double.parseDouble(System.getProperty("forest.twigs", "60")),
                Double.parseDouble(System.getProperty("forest.boughs", "200")),
                Double.parseDouble(System.getProperty("forest.limbs", "600")),
                Double.parseDouble(System.getProperty("forest.far", "2000")));
    }

    /** The level a sample so many centimetres from the eye is drawn at; none, beyond the furthest. */
    public Optional<Stage> level(final double away) {
        final double metres = away / METRE;
        return metres > far ? Optional.empty() : Optional.of(metres <= fine ? Stage.FINE
                : metres <= twigs ? Stage.TWIGS_GONE : metres <= boughs ? Stage.BOUGHS
                : metres <= limbs ? Stage.LIMBS : Stage.WHOLE);
    }
}

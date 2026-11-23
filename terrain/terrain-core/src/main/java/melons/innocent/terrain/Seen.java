package melons.innocent.terrain;

/**
 * A ground as seen at a scale: nothing finer than {@code scale} told apart, as when it is drawn with points that far
 * apart. How high it is at a spot is how high the ground is round it on the average, weighed the more the nearer, as
 * by a bell {@code scale} across: at the spot, at six round it half the scale off and six more round it the whole
 * scale off. So a fold sharper than the scale is rounded, a groove narrower than it is shallowed out, and points that
 * far apart do not alias what lies between them into steps; a slope that is even stays as it is. The ground's own,
 * the same every time; what lies on it untouched. Its extent is the ground's.
 *
 * @param ground what is seen
 * @param scale  how fine it is seen, in centimetres; nothing, and it is the ground itself
 */
public record Seen(Ground ground, double scale) implements Ground {

    /** How many points round the spot each ring has. */
    private static final int ROUND = 6;
    /** How much a point half the scale off counts, the spot's own counting one: as a bell half the scale wide. */
    private static final double HALF = Math.exp(-0.5);
    /** How much a point the whole scale off counts. */
    private static final double WHOLE = Math.exp(-2);

    public Seen {
        if (!(scale >= 0) || !Double.isFinite(scale)) {
            throw new IllegalArgumentException("a ground seen at some scale, not " + scale);
        }
    }

    @Override
    public Extent extent() {
        return ground.extent();
    }

    @Override
    public double height(final double x, final double z) {
        final double own = ground.height(x, z);
        if (scale == 0) {
            return own;
        }
        double sum = own;
        double weights = 1;
        for (int k = 0; k < ROUND; k++) {
            final double near = 2 * Math.PI * k / ROUND;
            final double far = near + Math.PI / ROUND;
            sum += HALF * ground.height(x + scale / 2 * Math.cos(near), z + scale / 2 * Math.sin(near));
            sum += WHOLE * ground.height(x + scale * Math.cos(far), z + scale * Math.sin(far));
            weights += HALF + WHOLE;
        }
        return sum / weights;
    }
}

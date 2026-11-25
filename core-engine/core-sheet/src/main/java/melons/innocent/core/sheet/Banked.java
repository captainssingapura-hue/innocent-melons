package melons.innocent.core.sheet;

/**
 * A flat profile, level or tilted one way: a deck, a road falling to one side, a bend banked toward its inside.
 *
 * @param from its left edge, so far to the line's right; less than nothing, to its left
 * @param to   its right edge
 * @param tilt how much higher it is a centimetre further to the right; nothing, level across
 */
public record Banked(double from, double to, double tilt) implements Profile {

    public Banked {
        if (!(to > from)) {
            throw new IllegalArgumentException("a profile from its left edge to its right: " + from + ", " + to);
        }
    }

    /** A level profile as far each side of the line as {@code half}. */
    public Banked(final double half) {
        this(-half, half, 0);
    }

    @Override
    public double high(final double aside) {
        return tilt * aside;
    }

    @Override
    public double rises(final double aside) {
        return tilt;
    }
}

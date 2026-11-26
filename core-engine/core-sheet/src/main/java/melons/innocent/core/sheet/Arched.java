package melons.innocent.core.sheet;

/**
 * A profile that is part of a circle standing upright: an arch. Swept along a straight line it is a vault; swept
 * round a ring from the ring inward to its middle, a dome.
 *
 * @param from   its left edge, so far to the line's right; less than nothing, to its left
 * @param to     its right edge
 * @param middle how far to the line's right its highest point is
 * @param radius the radius of the circle it is part of, in centimetres
 * @param crown  how high its highest point is over the line's height
 */
public record Arched(double from, double to, double middle, double radius, double crown) implements Profile {

    public Arched {
        if (!(to > from) || !(radius > 0) || Math.max(Math.abs(from - middle), Math.abs(to - middle)) >= radius) {
            throw new IllegalArgumentException("an arch between its edges, on a circle wider than it: " + from + ", "
                    + to + ", " + middle + ", " + radius);
        }
    }

    /**
     * An arch as far each side of the line as {@code half}, its edges at the line's height and its middle
     * {@code rise} higher.
     */
    public Arched(final double half, final double rise) {
        this(-half, half, 0, (half * half + rise * rise) / (2 * rise), rise);
    }

    /** How far under the circle's top its middle line is, so far {@code aside}: the upright side of the triangle. */
    private double upright(final double aside) {
        return Math.sqrt(radius * radius - (aside - middle) * (aside - middle));
    }

    @Override
    public double high(final double aside) {
        return crown - radius + upright(aside);
    }

    @Override
    public double rises(final double aside) {
        return -(aside - middle) / upright(aside);
    }
}

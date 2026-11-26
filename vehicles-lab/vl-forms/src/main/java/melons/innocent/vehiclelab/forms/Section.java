package melons.innocent.vehiclelab.forms;

/**
 * A tube's section where it is cut square to its line: {@code wide} across from side to side, {@code high} from top
 * to bottom, and how square it is: 2 round or oval, more toward a box with rounded corners, as a hydroformed tube is
 * pressed out to its die. Where it is round, it faces out from its middle; elsewhere, square to its own outline.
 *
 * @param wide  how wide it is, side to side
 * @param high  how high it is, top to bottom
 * @param round how square it is: 2 an oval, more toward a box
 */
public record Section(double wide, double high, double round) {

    public Section {
        if (wide <= 0 || high <= 0 || round < 2) {
            throw new IllegalArgumentException("a section is wide and high, and at least round: " + wide + " by "
                    + high + ", " + round);
        }
    }

    /** A round section {@code across} across. */
    public Section(final double across) {
        this(across, across, 2);
    }

    /** The section {@code share} of the way from this one to {@code next}. */
    public Section toward(final Section next, final double share) {
        return new Section(wide + (next.wide - wide) * share, high + (next.high - high) * share,
                round + (next.round - round) * share);
    }

    /** Where its outline is at {@code turn} round from its side, as {across, up} from its middle. */
    public double[] at(final double turn) {
        final double c = Math.cos(turn);
        final double s = Math.sin(turn);
        return new double[]{Math.signum(c) * Math.pow(Math.abs(c), 2 / round) * wide / 2,
                Math.signum(s) * Math.pow(Math.abs(s), 2 / round) * high / 2};
    }

    /** Which way its outline faces at {@code turn} round from its side, as {across, up}, not made a unit. */
    public double[] facing(final double turn) {
        final double[] at = at(turn);
        final double x = 2 * at[0] / wide;
        final double y = 2 * at[1] / high;
        return new double[]{Math.signum(x) * Math.pow(Math.abs(x), round - 1) / wide,
                Math.signum(y) * Math.pow(Math.abs(y), round - 1) / high};
    }
}

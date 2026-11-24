package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;

/**
 * A straight course, {@code length} long, heading along, its middle at nought: {@code level} high at its start, and
 * climbing {@code grade}, rise over run, from there. Level, it is laid in pieces of any length; climbing, in pieces
 * rising no more than {@link #RISE} each.
 *
 * @param length how long it is
 * @param level  how high it is at its start
 * @param grade  how steeply it climbs
 */
public record Straight(double length, double level, double grade) implements Course {

    /** How much a piece of a climbing course rises, at most. */
    static final double RISE = 12;

    public Straight {
        if (!(length > 0) || !(Math.abs(grade) <= 0.2)) {
            throw new IllegalArgumentException("a course some way long, no steeper than 20%: " + length + ", "
                    + grade);
        }
    }

    /** One level all the way. */
    public Straight(final double length, final double level) {
        this(length, level, 0);
    }

    @Override
    public Point at(final double s) {
        return new Point(0, s - length / 2, level + grade * s);
    }

    @Override
    public double heading(final double s) {
        return 0;
    }

    @Override
    public double piece() {
        return grade == 0 ? Double.POSITIVE_INFINITY : RISE / Math.abs(grade);
    }
}

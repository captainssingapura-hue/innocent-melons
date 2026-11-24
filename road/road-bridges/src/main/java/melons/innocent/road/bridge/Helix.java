package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import melons.innocent.road.surface.Course;

/**
 * A course climbing round and round its middle, at nought: {@code radius} out from it, {@code turns} times round, so
 * many turns or part of one, rising {@code rise} each turn, {@code level} high where it starts, due across of its
 * middle on the near side, heading along. Its inside is to the right of it, across from its line; so its right edge
 * turns closest round its middle. Laid in pieces turning no more than {@link #TURN} each, and rising no more than
 * {@link #RISE}.
 *
 * @param radius how far its line is from its middle
 * @param rise   how much it climbs each turn
 * @param turns  how many times it goes round
 * @param level  how high it is where it starts
 */
public record Helix(double radius, double rise, double turns, double level) implements Course {

    /** How far round a piece laid along it turns, at most, in radians; how much it rises, at most. */
    static final double TURN = Math.toRadians(6);
    static final double RISE = 12;

    public Helix {
        if (!(radius > 0) || !(rise > 0) || !(turns > 0)) {
            throw new IllegalArgumentException("a helix some way round, climbing: " + radius + ", " + rise + ", "
                    + turns);
        }
    }

    /** How steeply it climbs, rise over run. */
    public double grade() {
        return rise / (2 * Math.PI * radius);
    }

    /** How far round its middle it is {@code s} along it, in radians. */
    public double round(final double s) {
        return s / radius;
    }

    @Override
    public double length() {
        return 2 * Math.PI * radius * turns;
    }

    @Override
    public Point at(final double s) {
        final double a = round(s);
        return new Point(-radius * Math.cos(a), radius * Math.sin(a), level + grade() * s);
    }

    @Override
    public double heading(final double s) {
        return round(s);
    }

    @Override
    public double piece() {
        return Math.min(radius * TURN, RISE / grade());
    }
}

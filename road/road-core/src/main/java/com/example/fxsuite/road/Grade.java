package com.example.fxsuite.road;

/**
 * How a road rises along, in percent: rising in its direction of stations above nothing, falling below it, level at
 * nothing. Its order runs from falling most to rising most.
 *
 * @param percent the rise along, in percent
 */
public record Grade(double percent) implements RoadProfileAttribute<Grade> {

    public Grade {
        if (!Double.isFinite(percent)) {
            throw new IllegalArgumentException("a road rises along by so much, not " + percent);
        }
    }

    @Override
    public int compareTo(final Grade other) {
        return Double.compare(percent, other.percent);
    }
}

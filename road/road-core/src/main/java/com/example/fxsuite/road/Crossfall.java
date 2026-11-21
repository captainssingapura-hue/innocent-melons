package com.example.fxsuite.road;

/**
 * How a road falls across, in percent: falling to the right of its direction of stations above nothing, to the left
 * below it, level at nothing. Its order runs from falling most to the left to falling most to the right.
 *
 * @param percent the fall across, in percent
 */
public record Crossfall(double percent) implements RoadProfileAttribute<Crossfall> {

    public Crossfall {
        if (!Double.isFinite(percent)) {
            throw new IllegalArgumentException("a road falls across by so much, not " + percent);
        }
    }

    @Override
    public int compareTo(final Crossfall other) {
        return Double.compare(percent, other.percent);
    }
}

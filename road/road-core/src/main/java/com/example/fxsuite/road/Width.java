package com.example.fxsuite.road;

/**
 * The width of a strip across a road, a lane, a verge, a footway, in metres. A strip is added where it widens from
 * nothing, and dropped where it narrows to nothing; the number of lanes is no attribute of its own.
 *
 * @param metres how wide, nothing or more
 */
public record Width(double metres) implements RoadProfileAttribute<Width> {

    public Width {
        if (!(metres >= 0) || !Double.isFinite(metres)) {
            throw new IllegalArgumentException("a strip is nothing wide or wider, not " + metres);
        }
    }

    @Override
    public int compareTo(final Width other) {
        return Double.compare(metres, other.metres);
    }
}

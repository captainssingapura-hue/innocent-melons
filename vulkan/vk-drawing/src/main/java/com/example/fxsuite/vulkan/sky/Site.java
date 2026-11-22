package com.example.fxsuite.vulkan.sky;

/**
 * A place on the Earth, for the sky to be worked out over: how far north of the equator it is and how far east of
 * Greenwich, in degrees; south and west are less than nothing.
 */
public record Site(double latitude, double longitude) {

    public Site {
        if (!(Math.abs(latitude) <= 90 && Math.abs(longitude) <= 180)) {
            throw new IllegalArgumentException("a place is no further north or south than a pole, nor further east"
                    + " or west than half round: " + latitude + ", " + longitude);
        }
    }
}

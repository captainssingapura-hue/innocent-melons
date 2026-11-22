package com.example.fxsuite.vulkan.sky;

/**
 * Where something stands in the sky, as seen from a place: how high above level it is, in degrees, less than
 * nothing when it is below the horizon; and which way round it stands, in degrees from north round through east,
 * from 0 up to 360.
 */
public record Stands(double high, double round) {

    /** Whether it is above the horizon. */
    public boolean up() {
        return high > 0;
    }
}

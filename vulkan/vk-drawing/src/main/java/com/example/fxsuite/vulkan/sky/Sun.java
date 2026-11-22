package com.example.fxsuite.vulkan.sky;

import java.time.Instant;

/**
 * Where the sun is among the stars at a moment, by the almanac's short reckoning: good to a hundredth of a degree
 * for a century either side of 2000, which is far finer than anything lit by it shows.
 */
record Sun() {

    Among at(final Instant when) {
        final double days = new Epoch(when).days();
        final double mean = 280.460 + 0.9856474 * days;
        final double anomaly = Math.toRadians(357.528 + 0.9856003 * days);
        final double along = Math.toRadians(mean + 1.915 * Math.sin(anomaly) + 0.020 * Math.sin(2 * anomaly));
        final double tilt = Math.toRadians(23.439 - 0.0000004 * days);
        return new Among(Math.toDegrees(Math.atan2(Math.cos(tilt) * Math.sin(along), Math.cos(along))),
                Math.toDegrees(Math.asin(Math.sin(tilt) * Math.sin(along))));
    }
}

package com.example.fxsuite.vulkan.sky;

import java.time.Instant;

/**
 * Where the moon is among the stars at a moment, by the almanac's short reckoning: good to about a third of a
 * degree, less than the moon's own width. And how much nearer the horizon it stands for being seen from the
 * Earth's surface and not its middle, the moon being near enough for that to show: nearly a degree when it is low.
 */
record Moon() {

    Among at(final Instant when) {
        final double t = new Epoch(when).centuries();
        final double along = Math.toRadians(218.32 + 481267.881 * t
                + 6.29 * sine(135.0 + 477198.87 * t) - 1.27 * sine(259.3 - 413335.36 * t)
                + 0.66 * sine(235.7 + 890534.22 * t) + 0.21 * sine(269.9 + 954397.74 * t)
                - 0.19 * sine(357.5 + 35999.05 * t) - 0.11 * sine(186.5 + 966404.03 * t));
        final double off = Math.toRadians(5.13 * sine(93.3 + 483202.02 * t) + 0.28 * sine(228.2 + 960400.89 * t)
                - 0.28 * sine(318.3 + 6003.15 * t) - 0.17 * sine(217.6 - 407332.21 * t));
        // from the plane the Earth goes round in to the plane of its equator: the tilt between them, turned through
        final double x = Math.cos(off) * Math.cos(along);
        final double y = 0.9175 * Math.cos(off) * Math.sin(along) - 0.3978 * Math.sin(off);
        final double z = 0.3978 * Math.cos(off) * Math.sin(along) + 0.9175 * Math.sin(off);
        return new Among(Math.toDegrees(Math.atan2(y, x)), Math.toDegrees(Math.asin(z)));
    }

    /** How big the Earth's half-width looks from the moon at {@code when}, in degrees: what lowers the moon. */
    double lowering(final Instant when) {
        final double t = new Epoch(when).centuries();
        return 0.9508 + 0.0518 * cosine(135.0 + 477198.87 * t) + 0.0095 * cosine(259.3 - 413335.36 * t)
                + 0.0078 * cosine(235.7 + 890534.22 * t) + 0.0028 * cosine(269.9 + 954397.74 * t);
    }

    private double sine(final double degrees) {
        return Math.sin(Math.toRadians(degrees));
    }

    private double cosine(final double degrees) {
        return Math.cos(Math.toRadians(degrees));
    }
}

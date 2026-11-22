package com.example.fxsuite.vulkan.sky;

import java.time.Instant;

/**
 * Where something is among the stars: how far round the sky's equator from the spring point, and how far north of
 * that equator, in degrees. From this, a place and a moment, where it stands in that place's sky.
 */
record Among(double round, double north) {

    private static final double WHOLE = 360;

    /** Where it stands over {@code site} at {@code when}, seen from the Earth's middle. */
    Stands stands(final Site site, final Instant when) {
        final double hour = Math.toRadians(new Epoch(when).turned(site.longitude()) - round);
        final double place = Math.toRadians(site.latitude());
        final double tilt = Math.toRadians(north);
        final double high = Math.asin(Math.sin(place) * Math.sin(tilt)
                + Math.cos(place) * Math.cos(tilt) * Math.cos(hour));
        final double northward = Math.cos(place) * Math.sin(tilt)
                - Math.sin(place) * Math.cos(tilt) * Math.cos(hour);
        final double eastward = -Math.cos(tilt) * Math.sin(hour);
        return new Stands(Math.toDegrees(high),
                (Math.toDegrees(Math.atan2(eastward, northward)) + WHOLE) % WHOLE);
    }

    /** How far apart in the sky this and {@code other} are, in degrees. */
    double from(final Among other) {
        final double mine = Math.toRadians(north);
        final double theirs = Math.toRadians(other.north);
        return Math.toDegrees(Math.acos(Math.clamp(Math.sin(mine) * Math.sin(theirs)
                + Math.cos(mine) * Math.cos(theirs) * Math.cos(Math.toRadians(round - other.round)), -1, 1)));
    }
}

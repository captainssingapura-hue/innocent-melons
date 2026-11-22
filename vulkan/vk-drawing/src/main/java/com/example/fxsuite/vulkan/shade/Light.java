package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The light a frame is lit by: the way the sun shines (the way its light travels, in the world's frame), the sun's
 * light, and the light that falls on everything alike; and what a surface that mirrors its surroundings has to
 * mirror, the sky's light from above and the ground's from below. Each light is as much red, green and blue as it
 * has.
 *
 * <p>Where the sun stands can be said, and asked, as two angles instead: how high it is above level, and which way
 * round it stands, in degrees from ahead along the world (the way its third number grows) round toward its right.
 */
public record Light(Vec3 sunTowards, Vec3 sun, Vec3 ambient, Vec3 sky, Vec3 ground) {

    private static final double WHOLE = 360;

    /** A light with nothing above or below to be mirrored. */
    public Light(final Vec3 sunTowards, final Vec3 sun, final Vec3 ambient) {
        this(sunTowards, sun, ambient, new Vec3(0, 0, 0), new Vec3(0, 0, 0));
    }

    /** The same light over other ground: what is mirrored from below is {@code ground}. */
    public Light over(final Vec3 ground) {
        return new Light(sunTowards, sun, ambient, sky, ground);
    }

    /** The same light, its sun shining as {@code sun} says: as much red, green and blue as it has. */
    public Light shining(final Vec3 sun) {
        return new Light(sunTowards, sun, ambient, sky, ground);
    }

    /** How high the sun stands, in degrees above level: 90 straight overhead. */
    public double sunHigh() {
        return Math.toDegrees(Math.asin(-sunTowards.unit().y()));
    }

    /** Which way round the sun stands, in degrees from 0 up to 360; nothing, for a sun straight overhead. */
    public double sunRound() {
        final Vec3 toSun = sunTowards.unit().times(-1);
        return (Math.toDegrees(Math.atan2(toSun.x(), toSun.z())) + WHOLE) % WHOLE;
    }

    /**
     * The same light, the sun standing {@code high} degrees above level and {@code round} degrees round: as bright
     * and of the same colour, shining from there.
     */
    public Light sunAt(final double high, final double round) {
        final double up = Math.toRadians(high);
        final double about = Math.toRadians(round);
        final Vec3 toSun = new Vec3(Math.cos(up) * Math.sin(about), Math.sin(up), Math.cos(up) * Math.cos(about));
        return new Light(toSun.times(-1), sun, ambient, sky, ground);
    }
}

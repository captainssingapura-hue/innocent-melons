package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.sky.Heavens;
import com.example.fxsuite.vulkan.sky.Site;
import com.example.fxsuite.vulkan.sky.Sky;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.worked.Daylight;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The light and the sky of each frame of a walk, as the JavaFX window last asked for them: by hand, the sun where
 * its two sliders put it, by day or by night; or by the almanac and the clock, the sun and the moon where they
 * truly stand over a place on the Earth at a moment, the moment running on from where it was set as fast as the
 * clock is asked to run.
 *
 * <p>The clock is driven by the simulation's own steps, not by how long frames take, so a run told the same things
 * comes to the same moment. The world's ahead, the way the avatar faces when it starts, is north, and its right is
 * east. The place walked is level grass: what a metal mirrors from below is that grass, as the hour lights it.
 */
public final class Skies {

    private static final double BILLION = 1e9;
    /** The sign for degrees. */
    private static final char DEGREE = 0xb0;

    private final Asked asked;
    /** The bench's day and its night, each over grass as it lights it. */
    private final Daylight day;
    private final Daylight night;
    /** What the clock last began from, and the moment it has come to since. */
    private Asked.Almanac began;
    private Instant now;

    /** The skies {@code asked} asks for. */
    public Skies(final Asked asked) {
        final Daylight plain = new Daylight();
        final Daylight dark = plain.night();
        this.asked = asked;
        this.day = new Daylight(plain.light().over(new Vec3(0.48, 0.55, 0.37)), plain.sky());
        this.night = new Daylight(dark.light().over(new Vec3(0.09, 0.12, 0.10)), dark.sky());
        this.began = asked.almanac();
        this.now = began.begins();
    }

    /**
     * The clock taken on by {@code seconds} of the simulation's time: so many seconds, times as fast as it is asked
     * to run. Asked to begin again, it begins again from the moment asked.
     */
    public void stepped(final double seconds) {
        if (asked.almanac() != began) {
            began = asked.almanac();
            now = began.begins();
        }
        now = now.plusNanos(Math.round(seconds * asked.quickened() * BILLION));
    }

    /** The moment the clock has come to. */
    public Instant now() {
        return now;
    }

    /** Where the sun and the moon stand at that moment, over the place the clock began at. */
    public Sky sky() {
        return new Heavens(new Site(began.latitude(), began.longitude())).at(now);
    }

    /** This frame's light and sky. */
    public Daylight lit() {
        if (asked.clocked()) {
            return day.under(sky(), night);
        }
        final Daylight time = asked.night() ? night : day;
        final Light set = time.light().sunAt(asked.sun().high(), asked.sun().round());
        return new Daylight(set, time.sky());
    }

    /** The moment by the place's clocks, and where the sun and the moon stand at it, said in two lines. */
    public String said() {
        final Sky sky = sky();
        return DateTimeFormatter.ofPattern("EEE d MMM uuuu HH:mm", Locale.ENGLISH).format(now.atZone(began.zone()))
                + " (" + began.zone() + ")\n"
                + String.format(Locale.ENGLISH, "Sun %.0f%c up, %.0f%c round. Moon %.0f%c up, %.0f%% lit",
                        sky.sun().high(), DEGREE, sky.sun().round(), DEGREE, sky.moon().high(), DEGREE,
                        100 * sky.moonLit());
    }
}

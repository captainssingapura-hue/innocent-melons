package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.sky.Site;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the bench's controls ask of whoever draws. The controls are JavaFX's and live on its thread; the card is
 * used by one thread of its own, and the way the subject is looked at is that thread's too, since the mouse moves
 * it in the card's window. Nothing of the card is touched from JavaFX's thread: what is wanted is left here, each
 * thing whole and never changed once said, and the card's thread asks as each frame begins.
 */
public final class Asked {

    private final AtomicBoolean stopped = new AtomicBoolean();
    private final AtomicBoolean logged = new AtomicBoolean();
    private final AtomicBoolean first = new AtomicBoolean();
    private final AtomicReference<Double> further;
    private final AtomicBoolean shadowed;
    private final AtomicReference<Sun> sun;
    private final AtomicReference<Shadowing> shadowing = new AtomicReference<>(Shadowing.RAYS);
    /** The ways of finding shadows there are to choose from: every way, until the card's thread says which. */
    private final AtomicReference<List<Shadowing>> offered = new AtomicReference<>(List.of(Shadowing.values()));
    private final AtomicBoolean night = new AtomicBoolean();
    private final AtomicBoolean clocked = new AtomicBoolean();
    private final AtomicReference<Double> quickened = new AtomicReference<>(60.0);
    /** To begin with: over Marina Bay, from the moment this was made, by Singapore's clocks. */
    private final AtomicReference<Almanac> almanac = new AtomicReference<>(
            new Almanac(1.283, 103.86, Instant.now(), ZoneId.of("Asia/Singapore")));

    /**
     * What the sun and the moon are worked out from: a place on the Earth, in degrees north and east; the moment
     * the clock begins from; and whose clocks the moment is told by.
     */
    public record Almanac(double latitude, double longitude, Instant begins, ZoneId zone) {

        /**
         * This, changed as it is told in words, a blank leaving a thing as it is: {@code at} so far north, a
         * comma, and so far east, in degrees; {@code zone} whose clocks, by name, as {@code Asia/Singapore};
         * {@code when} a date and an hour by those clocks, as {@code 2026-06-21T12:00}. What cannot be read, or
         * is no place on the Earth, is refused, saying why.
         */
        public Almanac told(final String at, final String when, final String zone) {
            final ZoneId by = zone.isBlank() ? this.zone : ZoneId.of(zone.trim());
            final String[] place = at.split(",");
            if (!at.isBlank() && place.length != 2) {
                throw new IllegalArgumentException("a place is so far north, a comma, and so far east: " + at);
            }
            final Site stood = at.isBlank() ? new Site(latitude, longitude)
                    : new Site(Double.parseDouble(place[0].trim()), Double.parseDouble(place[1].trim()));
            final Instant from = when.isBlank() ? begins : LocalDateTime.parse(when.trim()).atZone(by).toInstant();
            return new Almanac(stood.latitude(), stood.longitude(), from, by);
        }
    }

    /** Where the sun is asked to stand: how many degrees above level, and how many round. */
    public record Sun(double high, double round) {
    }

    /** Asking, to begin with, for the first of the row to be shown as from {@code further} times as far. */
    public Asked(final double further) {
        this(further, false, new Sun(90, 0));
    }

    /**
     * The same, asking to begin with for shadows to be drawn, or not, as {@code shadowed} says, and for the sun to
     * stand where {@code sun} has it.
     */
    public Asked(final double further, final boolean shadowed, final Sun sun) {
        this.further = new AtomicReference<>(further);
        this.shadowed = new AtomicBoolean(shadowed);
        this.sun = new AtomicReference<>(sun);
    }

    /** Asks for the sun to stand {@code high} degrees above level and {@code round} degrees round, from now on. */
    public void sun(final double high, final double round) {
        sun.set(new Sun(high, round));
    }

    /** Where the sun was last asked to stand. */
    public Sun sun() {
        return sun.get();
    }

    /** Asks for night, or for day, from the next frame on. It is day until night is asked for. */
    public void night(final boolean fallen) {
        night.set(fallen);
    }

    /** Whether night was last asked for. */
    public boolean night() {
        return night.get();
    }

    /**
     * Asks for the sun and the moon to stand where the almanac and the clock put them, or, with false, where the
     * sliders and the night box do. They stand by the sliders until this is asked.
     */
    public void clocked(final boolean byClock) {
        clocked.set(byClock);
    }

    /** Whether the sun and the moon were last asked to stand by the almanac and the clock. */
    public boolean clocked() {
        return clocked.get();
    }

    /** Asks for the clock to run {@code times} as fast as time does; nothing, for a clock that stands still. */
    public void quickened(final double times) {
        quickened.set(times);
    }

    /** How many times as fast as time the clock was last asked to run. */
    public double quickened() {
        return quickened.get();
    }

    /** Asks for the clock to begin again as {@code almanac} says: at its place, from its moment. */
    public void almanac(final Almanac almanac) {
        this.almanac.set(almanac);
    }

    /** What the clock was last asked to begin from. Each asking is a new one, even when it says the same. */
    public Almanac almanac() {
        return almanac.get();
    }

    /** Asks for shadows to be found {@code by} that way, from the next frame on. */
    public void shadowing(final Shadowing by) {
        shadowing.set(by);
    }

    /**
     * The same, the way said in a word as a run is told it ({@code picture}, {@code rays}); a blank leaves it as
     * it is, and a word that is no way is refused.
     */
    public void shadowing(final String said) {
        if (!said.isBlank()) {
            shadowing.set(Shadowing.valueOf(said.trim().toUpperCase(Locale.ROOT)));
        }
    }

    /**
     * The way shadows were last asked to be found: by rays, until another is asked for. On a card that answers
     * no rays they are found from the sun's picture all the same, and the window offers only that.
     */
    public Shadowing shadowing() {
        return shadowing.get();
    }

    /** Says which ways of finding shadows there are to choose from, on the card that draws. */
    public void offered(final List<Shadowing> ways) {
        offered.set(List.copyOf(ways));
    }

    /** The ways of finding shadows there are to choose from. */
    public List<Shadowing> offered() {
        return offered.get();
    }

    /** Asks for shadows to be drawn, or not, from the next frame on. */
    public void shadowed(final boolean drawn) {
        shadowed.set(drawn);
    }

    /** Whether shadows were last asked to be drawn. */
    public boolean shadowed() {
        return shadowed.get();
    }

    /** Asks for the first of the row to be shown as from {@code times} times as far, from the next frame on. */
    public void further(final double times) {
        further.set(times);
    }

    /** How many times as far the first of the row was last asked to be shown as from. */
    public double further() {
        return further.get();
    }

    /** Asks for the subject to be looked at again as it was to begin with. */
    public void first() {
        first.set(true);
    }

    /** Whether the first view has been asked for since this was last asked; once answered, it is not wanted again. */
    public boolean firstWanted() {
        return first.getAndSet(false);
    }

    /** Whether what the bench tells of itself, a frame's time and the memory held, is logged as it is told. */
    public void logged(final boolean on) {
        logged.set(on);
    }

    public boolean logged() {
        return logged.get();
    }

    /** Asks for the bench to stop. */
    public void stop() {
        stopped.set(true);
    }

    public boolean stopped() {
        return stopped.get();
    }
}

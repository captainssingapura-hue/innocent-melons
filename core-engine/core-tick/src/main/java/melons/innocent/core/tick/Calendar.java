package melons.innocent.core.tick;

import java.time.Instant;

/**
 * The calendar of a world: the moment its first tick begins at, and how fast the calendar goes against the ticks.
 * The moment of any tick is worked out from its count, so the hour of the day, and with it the light and the
 * weather, is a matter of which tick it is and of nothing else: no step reads a clock.
 *
 * <p>Begun at the real moment and going at the ticks' own pace, a world keeps the real clock's hour for as long as
 * it is ticked in step with it. Where it has fallen behind, the calendar is set again ({@link #set}), which is
 * something asked of the world as anything else is, and not something a step does of itself.
 *
 * @param began the moment the world's first tick begins at
 * @param pace  how many seconds of the calendar go by in a second of ticks: one, for a world that keeps time's own
 *              pace; nothing, for one whose hour stands still
 */
public record Calendar(Instant began, double pace) {

    private static final double NANOS = 1e9;

    /** A calendar begun at {@code began} and going at the ticks' own pace. */
    public Calendar(final Instant began) {
        this(began, 1);
    }

    /** The moment {@code tick} begins at. */
    public Instant at(final Tick tick) {
        return began.plusNanos(Math.round(tick.count() * (NANOS * pace / tick.rate())));
    }

    /** This calendar set again so that {@code tick} begins at {@code moment}, going on at the same pace. */
    public Calendar set(final Tick tick, final Instant moment) {
        return new Calendar(moment.minusNanos(Math.round(tick.count() * (NANOS * pace / tick.rate()))), pace);
    }
}

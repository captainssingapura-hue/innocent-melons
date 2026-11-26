package melons.innocent.core.tick;

/**
 * One tick of a world: which it is, counted from nothing at the world's beginning, and how many there are to a
 * second. It is what a world's time is: the state before a tick and what is asked at it give the state after, and
 * the tick after is the next count. An engine is handed the tick it is to work out, apart from the state, and takes
 * from it how long the tick is and, where it goes by a timetable, how long the world has gone on.
 *
 * <p>Nothing here is added up. How long the world has gone on is the count over the rate, worked out afresh each
 * time it is asked, so it is the same at the same tick however the world came there, and two worlds of one rate
 * agree on a moment by agreeing on a count.
 *
 * @param count how many ticks were taken before this one, since the world began
 * @param rate  how many ticks there are to a second
 */
public record Tick(long count, int rate) {

    public Tick {
        if (count < 0 || rate < 1) {
            throw new IllegalArgumentException("a tick is counted from nothing, at one a second or more: " + count
                    + " at " + rate + " a second");
        }
    }

    /** How long this tick is, in seconds: the same for every tick of its world. */
    public double seconds() {
        return 1.0 / rate;
    }

    /** How long the world had gone on when this tick began, in seconds. */
    public double elapsed() {
        return (double) count / rate;
    }

    /** The tick after this one. */
    public Tick next() {
        return new Tick(count + 1, rate);
    }
}

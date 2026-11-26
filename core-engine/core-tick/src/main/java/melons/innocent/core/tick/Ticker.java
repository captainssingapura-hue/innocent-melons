package melons.innocent.core.tick;

/**
 * The one ticker of a world: how many ticks there are to a second, said once, when the world is made, and never
 * again. Every tick of the world is made by it, so every engine that steps anything in the world steps it by the
 * same tick, and a kept run, another player or a film names a moment of the world by its count alone.
 *
 * <p>It reads no clock. Whatever paces a world against a real clock, or draws a film as fast as it can, is outside
 * the world and says only how many ticks to take.
 *
 * @param rate how many ticks there are to a second
 */
public record Ticker(int rate) {

    /** As the game engine steps: a hundred and twenty to a second. */
    private static final int USUAL = 120;

    public Ticker {
        if (rate < 1) {
            throw new IllegalArgumentException("a ticker goes at one tick a second or more: " + rate);
        }
    }

    /** A ticker at a hundred and twenty ticks a second, as the game engine steps. */
    public Ticker() {
        this(USUAL);
    }

    /** The world's first tick, before which none has been taken. */
    public Tick first() {
        return at(0);
    }

    /** The tick before which {@code count} have been taken. */
    public Tick at(final long count) {
        return new Tick(count, rate);
    }

    /** How many ticks so many {@code seconds} are, to the nearest whole one. */
    public long ticks(final double seconds) {
        return Math.round(seconds * rate);
    }
}

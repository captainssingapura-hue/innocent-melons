package melons.innocent.core.motion;

/**
 * An observer's motion, detected: told where the observer is and which way it faces at each step of the simulation,
 * it says how the observer is moving ({@link Motion}). The same for whatever moves an eye, a walker, a flier, a
 * craft, a boat or a camera of a film, so everything that goes by speed asks one thing.
 *
 * <p>Its velocity and its turning are evened out over a short while, a quarter of a second unless said otherwise,
 * so that what is decided by them does not flicker with every small change of pace; told steps of the same length
 * in the same order it says the same every time, so a run kept and played again decides alike. It goes by the
 * simulation's steps and never by how long a frame took. An observer set down somewhere else, as a film cuts from
 * one camera to another, is {@link #placed}: it is there, and has not flown there.
 */
public final class Moving {

    private static final double TURN = 360;

    private final double evened;
    private Spot at;
    private double heading;
    private Spot going = new Spot(0, 0, 0);
    private double turning;

    /** An observer standing at {@code at} facing {@code heading}, its motion evened over a quarter of a second. */
    public Moving(final Spot at, final double heading) {
        this(at, heading, 0.25);
    }

    /** The same, its motion evened over {@code evened} seconds: not at all, for nothing. */
    public Moving(final Spot at, final double heading, final double evened) {
        if (!(evened >= 0)) {
            throw new IllegalArgumentException("evened over some while, or none: " + evened);
        }
        this.at = at;
        this.heading = heading;
        this.evened = evened;
    }

    /**
     * Told that, a step of {@code seconds} on, the observer is at {@code now} facing {@code facing}, in degrees
     * about the upright: its velocity and its turning move toward what that step shows, the more the longer the
     * step is against the while they are evened over.
     */
    public void stepped(final Spot now, final double facing, final double seconds) {
        if (!(seconds > 0)) {
            throw new IllegalArgumentException("a step of some length: " + seconds);
        }
        final double share = evened <= 0 ? 1 : 1 - Math.exp(-seconds / evened);
        final Spot shown = now.minus(at).times(1 / seconds);
        going = going.plus(shown.minus(going).times(share));
        // the short way round, so facing 359 and then 1 is a turn of two degrees and not of 358 the other way
        final double turned = ((facing - heading) % TURN + TURN + TURN / 2) % TURN - TURN / 2;
        turning += (turned / seconds - turning) * share;
        at = now;
        heading = facing;
    }

    /** Set down at {@code now} facing {@code facing}, standing still: not gone there. */
    public void placed(final Spot now, final double facing) {
        at = now;
        heading = facing;
        going = new Spot(0, 0, 0);
        turning = 0;
    }

    /** How the observer is moving now. */
    public Motion motion() {
        return new Motion(at, going, turning);
    }
}

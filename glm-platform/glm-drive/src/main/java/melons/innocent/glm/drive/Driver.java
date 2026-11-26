package melons.innocent.glm.drive;

/**
 * What the driver does, a moment at a time: how far the throttle and the brake are pressed, each nought to one; and
 * what was asked just now, each once as its key went down: a change up or down, a switch between the gearbox changing
 * by itself and the driver changing it, the cruise control set or let go, and its speed raised or lowered.
 *
 * @param throttle how far the throttle is pressed
 * @param brake    how far the brake is pressed
 * @param up       a change up asked for
 * @param down     a change down asked for
 * @param switched the gearbox switched between automatic and manual
 * @param cruise   the cruise control set, or let go
 * @param faster   the cruise control's speed raised
 * @param slower   the cruise control's speed lowered
 */
public record Driver(double throttle, double brake, boolean up, boolean down, boolean switched, boolean cruise,
                     boolean faster, boolean slower) {

    /** Doing nothing. */
    public Driver() {
        this(0, 0, false, false, false, false, false, false);
    }

    /** The throttle pressed {@code open} and nothing else. */
    public Driver(final double open) {
        this(open, 0, false, false, false, false, false, false);
    }
}

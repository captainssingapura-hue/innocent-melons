package melons.innocent.core.interaction;

/**
 * How power gathers while a trigger is held: from nothing, evenly, to all there is once it has been held so long,
 * and no more however much longer it is held. Let go, what has gathered is what the act is done with: a throw goes
 * as far as the trigger was held for.
 *
 * @param fills how long the trigger is held before all the power has gathered, in seconds
 */
public record Gathering(double fills) {

    public Gathering {
        if (!(fills > 0)) {
            throw new IllegalArgumentException("power gathers over some time: " + fills);
        }
    }

    /** Power that has all gathered in a second and a half. */
    public Gathering() {
        this(1.5);
    }

    /** How much of all the power has gathered once the trigger has been held so many {@code seconds}: 0 to 1. */
    public double share(final double seconds) {
        return Math.max(0, Math.min(1, seconds / fills));
    }
}

package melons.innocent.testkit.loose;

import melons.innocent.core.interaction.Gathering;

import java.util.Optional;

/**
 * The winding up of a throw, while a thing is held readied to be thrown: for as long as the trigger is held power
 * gathers, and the step it is let go in says how much had gathered, which is what the thing is thrown with. A
 * trigger never pulled lets nothing go.
 */
final class Windup {

    private final Gathering gathering = new Gathering();
    private double held;
    private boolean pulling;

    /**
     * A step of so many {@code seconds}, the trigger held through it or not: the share of all the power the thing
     * is let go with, in the step the trigger is let go; nothing, in every other.
     */
    Optional<Double> stepped(final boolean triggered, final double seconds) {
        if (triggered) {
            held += seconds;
            pulling = true;
            return Optional.empty();
        }
        if (!pulling) {
            return Optional.empty();
        }
        final double share = share();
        clear();
        return Optional.of(share);
    }

    /** How much of all the power has gathered so far: 0 to 1. */
    double share() {
        return gathering.share(held);
    }

    /** Nothing gathered, and the trigger not pulled. */
    void clear() {
        held = 0;
        pulling = false;
    }
}

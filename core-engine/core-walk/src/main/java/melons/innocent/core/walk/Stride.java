package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.step.Explanation;

/**
 * What the walking engine worked out for a walker in a tick: one of the ways it can come out, each with the
 * figures it came by.
 */
public sealed interface Stride extends Explanation<Walker> {

    /**
     * On its feet, going along what it stands on.
     *
     * @param on    what it stands on
     * @param grade how steep that is there: rise over run
     * @param asked how it was asked to go, against what it stands on
     * @param came  how it came to go, against what it stands on, eased toward what was asked as its footing lets
     * @param rose  how far its feet rose in the tick: down, where less than nothing
     */
    record Afoot(String on, double grade, Spot asked, Spot came, double rose) implements Stride {
    }

    /**
     * It would have gone where it cannot, and stayed where it was, carried only by what it stands on.
     *
     * @param by  what refused it
     * @param why how, in words
     */
    record Refused(String by, String why) implements Stride {
    }

    /**
     * It jumped.
     *
     * @param from what it left
     * @param up   how fast it left it, upward
     */
    record Jumped(String from, double up) implements Stride {
    }

    /**
     * It walked off an edge: nothing was under it within a step.
     *
     * @param from what it left
     */
    record SteppedOff(String from) implements Stride {
    }

    /**
     * In the air: pulled down, keeping its way.
     *
     * @param gained how its going changed in the tick
     */
    record Aloft(Spot gained) implements Stride {
    }

    /**
     * It came down on something.
     *
     * @param on   what it came down on
     * @param fell how fast it was coming down, against that
     */
    record CameDown(String on, double fell) implements Stride {
    }

    /** The one empty explanation, given every time while explaining is off. */
    enum Unsaid implements Stride {
        UNSAID
    }
}

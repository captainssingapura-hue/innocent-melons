package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Reckoned;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.step.Explanation;

import java.util.List;
import java.util.Optional;

/**
 * What the loose body worked out for a thing in a tick: one of the ways it can come out, each with the figures it
 * came by. Where it handed the thing on, to what rolls or to what has corners, or took a tick in parts, or put its
 * stepping right, the explanation of what it handed on to is inside its own, so the explanations are nested as the
 * engines that ran were.
 */
public sealed interface Why extends Explanation<Rigid> {

    /**
     * In flight, touched by nothing or thrown up off what it stood on.
     *
     * @param gained the velocity gravity gave it in the tick
     */
    record Flew(Spot gained) implements Why {
    }

    /**
     * It came down on something and was set down there, just touching.
     *
     * @param on   what it came down on
     * @param lost the velocity it came down with, against how that was going, all of which it lost
     */
    record CameDown(String on, Spot lost) implements Why {
    }

    /**
     * Balanced on what it touches: it did not move, and the tick was counted toward its rest.
     *
     * @param by    what holds it
     * @param still how many ticks running it has now been held
     */
    record Held(List<String> by, int still) implements Why {
    }

    /**
     * Touched but not balanced: turned about the line it is held along.
     *
     * @param reckoned the check's own reckoning: how its turning changes, and the line it tips about
     * @param turned   how far it was carried round that line, in radians
     * @param on       what it came down on and was stopped by, where it was
     */
    record Tipped(Reckoned reckoned, double turned, Optional<String> on) implements Why {
    }

    /**
     * Handed on to rolling: a thing of a shape that rolls, which keeps its motion.
     *
     * @param gained  how its velocity changed in the tick: gravity's pull, the push of what it presses on, the
     *                slowing of its rolling
     * @param on      what it pressed on
     * @param stopped whether it was brought to a stop
     */
    record Rolled(Spot gained, List<String> on, boolean stopped) implements Why {
    }

    /**
     * On something that is itself going: carried on with it, and stepped as it stands and moves against that.
     *
     * @param with   how what it is on was going
     * @param within what was worked out for it against that
     */
    record Carried(Spot with, Why within) implements Why {
    }

    /**
     * Going fast, it was taken through the tick in parts, so as not to pass through a thin thing.
     *
     * @param parts what was worked out in each part, in order
     */
    record InParts(List<Why> parts) implements Why {
    }

    /**
     * The first correction: its stepping put right for what stood in its path.
     *
     * @param regular what the regular stepping had worked out
     * @param met     what it met
     * @param lost    the velocity taken away, which was into what it met
     */
    record Corrected(Why regular, List<String> met, Spot lost) implements Why {
    }

    /** The one empty explanation, given every time while explaining is off. */
    enum Unsaid implements Why {
        UNSAID
    }
}

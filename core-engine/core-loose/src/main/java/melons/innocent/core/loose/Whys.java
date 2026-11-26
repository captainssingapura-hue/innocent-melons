package melons.innocent.core.loose;

import melons.innocent.core.step.Explaining;

import java.util.function.Supplier;

/**
 * The loose body's explanations as the run's switch has them: worked out where explaining is on, and where it is
 * off not worked out at all, the one empty explanation given in their place.
 *
 * @param explaining the run's switch
 */
record Whys(Explaining explaining) {

    /** What {@code why} works out, where explaining is on; the empty explanation, where it is off. */
    Why of(final Supplier<Why> why) {
        return explaining.on() ? why.get() : Why.Unsaid.UNSAID;
    }
}

package melons.innocent.core.walk;

import melons.innocent.core.step.Explaining;

import java.util.function.Supplier;

/**
 * The walking engine's explanations as the run's switch has them: worked out where explaining is on, and where it
 * is off not worked out at all, the one empty explanation given in their place.
 *
 * @param explaining the run's switch
 */
record Saying(Explaining explaining) {

    /** What {@code why} works out, where explaining is on; the empty explanation, where it is off. */
    Stride of(final Supplier<Stride> why) {
        return explaining.on() ? why.get() : Stride.Unsaid.UNSAID;
    }
}

package com.example.fxsuite.vulkan.term;

/**
 * A number counted in whole turns of something that comes round again: so many stripes round a fruit, so many boards
 * up a wall. {@code counted} is how many turns in a point is; {@code rate} how many turns there are to a centimetre
 * of the surface there, which is what says how much of a turn a pixel covers.
 */
public record Turns(Real counted, Real rate) {

    /** A pixel is never taken to cover less of a turn than this: the least a single can tell apart at these sizes. */
    private static final double FINEST = 1e-4;

    /**
     * How much of what a pixel covers is on, where each turn is on for its first {@code share} and off for the rest;
     * {@code covers} is how much of the surface the pixel covers, in centimetres. Where a pixel covers hardly any of
     * a turn it is 1 or 0, stripe or no stripe. As the pixel covers more it is the stripe's true share of what the
     * pixel covers, worked out exactly, not sampled: so far off, where a pixel covers many turns, it is
     * {@code share} itself, the stripes' average, with no shimmer on the way there.
     */
    public Real pulse(final double share, final Real covers) {
        final Real wide = rate.times(covers).most(new Real(FINEST));
        final Real half = wide.times(0.5);
        return on(counted.plus(half), share).less(on(counted.less(half), share)).over(wide);
    }

    /** How many turns' worth have been on, from nothing up to {@code upTo} turns. */
    private Real on(final Real upTo, final double share) {
        return upTo.floored().times(share).plus(upTo.part().least(new Real(share)));
    }
}

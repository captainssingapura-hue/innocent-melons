package com.example.fxsuite.road;

/**
 * A road's crossfall turned, from a lower to a higher: a camber run off, a fall to the left turned to a fall to the
 * right going into a bend. The carriageway turns about its centre line, its fall changing evenly along. Its length is
 * the one at which the carriageway's edge rises or falls against the centre line by no more than the relative gradient
 * the speed allows: 0.80 percent at 20 km/h, down to 0.50 at 80 and 0.35 at 130.
 *
 * @param lower the crossfall falling more to the left
 * @param higher the crossfall falling more to the right
 */
public record RunOff(Crossfall lower, Crossfall higher) implements RoadTransition<Crossfall> {

    private static final Transitions TRANSITIONS = Transitions.INSTANCE;
    private static final double SLOWEST = 0.80;
    private static final double AT_80 = 0.50;
    private static final double FASTEST = 0.35;

    public RunOff {
        TRANSITIONS.ordered(lower, higher);
    }

    @Override
    public Crossfall at(final double along) {
        return new Crossfall(lower.percent() + (higher.percent() - lower.percent()) * TRANSITIONS.along(along));
    }

    @Override
    public double length(final Design design) {
        return design.width() / 2 * (higher.percent() - lower.percent()) / relative(design.speed());
    }

    /** The steepest the edge may rise or fall against the centre line, in percent, at {@code speed}. */
    private double relative(final double speed) {
        final double gradient = speed <= 80 ? 0.90 - 0.005 * speed : AT_80 - 0.003 * (speed - 80);
        return Math.max(FASTEST, Math.min(SLOWEST, gradient));
    }
}

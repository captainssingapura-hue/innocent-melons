package com.example.fxsuite.road;

/**
 * A strip widening, from a lower width to a higher: a lane added from nothing, a carriageway eased wider. Its edge
 * follows a reverse curve, leaving the narrower width and meeting the wider along them, with no kink at either end.
 * Its length is the one the highway engineers' taper rule gives for the width it gains and the road's speed: the gain
 * times the speed squared over 155 below 70 km/h, the gain times the speed over 1.6 from 70.
 *
 * @param lower the narrower width
 * @param higher the wider
 */
public record Taper(Width lower, Width higher) implements RoadTransition<Width> {

    private static final Transitions TRANSITIONS = Transitions.INSTANCE;
    private static final double FAST = 70;
    private static final double SLOW_RULE = 155;
    private static final double FAST_RULE = 1.6;

    public Taper {
        TRANSITIONS.ordered(lower, higher);
    }

    @Override
    public Width at(final double along) {
        final double eased = (1 - Math.cos(Math.PI * TRANSITIONS.along(along))) / 2;
        return new Width(lower.metres() + (higher.metres() - lower.metres()) * eased);
    }

    @Override
    public double length(final Design design) {
        final double gained = higher.metres() - lower.metres();
        final double speed = design.speed();
        return speed < FAST ? gained * speed * speed / SLOW_RULE : gained * speed / FAST_RULE;
    }
}

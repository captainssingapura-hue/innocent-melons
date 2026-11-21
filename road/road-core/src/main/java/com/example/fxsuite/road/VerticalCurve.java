package com.example.fxsuite.road;

/**
 * A road's grade changed, from a lower to a higher: over a crest, through a sag, onto a bridge's deck. Its grade
 * changes evenly along, so the road's height follows a parabola. Its length is the grade's change times the rate the
 * speed's stopping sight distance asks for, the longer of a crest's and a sag's, since which it is depends on the way
 * it is laid; never shorter than 0.6 metres for each km/h.
 *
 * @param lower the grade falling more
 * @param higher the grade rising more
 */
public record VerticalCurve(Grade lower, Grade higher) implements RoadTransition<Grade> {

    private static final Transitions TRANSITIONS = Transitions.INSTANCE;
    private static final double REACTION = 2.5;
    private static final double SHORTEST = 0.6;

    public VerticalCurve {
        TRANSITIONS.ordered(lower, higher);
    }

    @Override
    public Grade at(final double along) {
        return new Grade(lower.percent() + (higher.percent() - lower.percent()) * TRANSITIONS.along(along));
    }

    @Override
    public double length(final Design design) {
        final double sight = sight(design.speed());
        final double crest = sight * sight / 658;
        final double sag = sight * sight / (120 + 3.5 * sight);
        final double rate = Math.max(crest, sag);
        return Math.max(rate * (higher.percent() - lower.percent()), SHORTEST * design.speed());
    }

    /** How far ahead a driver at {@code speed} must see to stop, in metres: reacting, then braking. */
    private double sight(final double speed) {
        return 0.278 * speed * REACTION + 0.039 * speed * speed / 3.4;
    }
}

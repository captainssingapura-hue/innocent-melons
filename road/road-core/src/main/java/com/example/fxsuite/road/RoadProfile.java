package com.example.fxsuite.road;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A road's profile at one station: every attribute of it ({@link RoadProfileAttribute}), one value each, how wide it
 * is, how it falls across and how it rises along. Between two profiles, the seam between two sections, lies a
 * transition of each attribute whose values differ, and none of those that do not.
 *
 * @param width how wide the carriageway is
 * @param crossfall how it falls across
 * @param grade how it rises along
 */
public record RoadProfile(Width width, Crossfall crossfall, Grade grade) implements Scalar {

    private static final Transitions TRANSITIONS = Transitions.INSTANCE;

    /** The transitions from this profile to {@code other}: one to each attribute that differs, in this order. */
    public List<RoadTransition<?>> to(final RoadProfile other) {
        return Stream.<Optional<? extends RoadTransition<?>>>of(
                TRANSITIONS.between(width, other.width, Taper::new),
                TRANSITIONS.between(crossfall, other.crossfall, RunOff::new),
                TRANSITIONS.between(grade, other.grade, VerticalCurve::new))
                .flatMap(Optional::stream).<RoadTransition<?>>map(transition -> transition).toList();
    }
}

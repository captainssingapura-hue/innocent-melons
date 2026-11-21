package com.example.fxsuite.road;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * What every transition is held to, and the transition between two values in whatever order they come: from the lower
 * to the higher, or none where they are equal.
 */
public record Transitions() implements StatelessFunctionalObject {

    public static final Transitions INSTANCE = new Transitions();

    /**
     * The transition {@code transition} makes between {@code one} and {@code other}, from whichever is lower to the
     * other; none where they are equal, since it would change nothing.
     */
    public <A extends RoadProfileAttribute<A>, T extends RoadTransition<A>> Optional<T> between(final A one,
            final A other, final BiFunction<A, A, T> transition) {
        final int order = one.compareTo(other);
        if (order == 0) {
            return Optional.empty();
        }
        return Optional.of(order < 0 ? transition.apply(one, other) : transition.apply(other, one));
    }

    /** Holds a transition's two values to their order: {@code lower} below {@code higher}. */
    public <A extends RoadProfileAttribute<A>> void ordered(final A lower, final A higher) {
        if (lower.compareTo(higher) >= 0) {
            throw new IllegalArgumentException("a transition runs from a lower value to a higher, not from " + lower
                    + " to " + higher);
        }
    }

    /** Holds a fraction of the way along a transition between its two ends. */
    public double along(final double along) {
        if (!(along >= 0 && along <= 1)) {
            throw new IllegalArgumentException("a transition runs from 0 to 1 along it, not " + along);
        }
        return along;
    }
}

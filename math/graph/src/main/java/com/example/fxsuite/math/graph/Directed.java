package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.Objects;

/**
 * An edge gone along one way only, from {@code from} to {@code to}: water down a roof, traffic along a one-way street,
 * a load from what bears to what bears it.
 *
 * @param from  where it is gone along from, and where what it carries begins
 * @param to    where it is gone along to
 * @param label what it carries
 * @param <V>   what its vertices are
 * @param <L>   what it carries
 */
public record Directed<V extends Scalar, L extends Scalar>(V from, V to, L label) implements Edge<V, L> {

    public Directed {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(label, "label");
        if (from.equals(to)) {
            throw new IllegalArgumentException("an edge from " + from + " to itself");
        }
    }

    @Override
    public V one() {
        return from;
    }

    @Override
    public V other() {
        return to;
    }

    @Override
    public boolean leaves(final V vertex) {
        return from.equals(vertex);
    }

    @Override
    public boolean enters(final V vertex) {
        return to.equals(vertex);
    }
}

package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.Objects;

/**
 * An edge gone along either way: a side of a face, a street open both ways. What it carries still begins at
 * {@code one}, a curve laid along it read backward when it is gone along from {@code other}.
 *
 * @param one   its first vertex, where what it carries begins
 * @param other its second vertex
 * @param label what it carries
 * @param <V>   what its vertices are
 * @param <L>   what it carries
 */
public record Undirected<V extends Scalar, L extends Scalar>(V one, V other, L label) implements Edge<V, L> {

    public Undirected {
        Objects.requireNonNull(one, "one");
        Objects.requireNonNull(other, "other");
        Objects.requireNonNull(label, "label");
        if (one.equals(other)) {
            throw new IllegalArgumentException("an edge from " + one + " to itself");
        }
    }

    @Override
    public boolean leaves(final V vertex) {
        return touches(vertex);
    }

    @Override
    public boolean enters(final V vertex) {
        return touches(vertex);
    }
}

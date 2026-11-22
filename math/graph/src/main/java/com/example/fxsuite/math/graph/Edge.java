package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * An edge: between two vertices, never from one to itself, carrying a label of its own kind (a curve laid along it, a
 * joint's kind, or nothing much: {@link Unlabelled}). Directed, it is gone along from its first vertex to its second
 * only; undirected, either way. Either way its first vertex is where what it carries begins, a curve's start.
 *
 * @param <V> what its vertices are
 * @param <L> what it carries
 */
public sealed interface Edge<V extends Scalar, L extends Scalar> extends Scalar
        permits Directed, Undirected {

    /** Its first vertex: where what it carries begins. */
    V one();

    /** Its second vertex. */
    V other();

    /** What it carries. */
    L label();

    /** Whether it may be gone along from {@code vertex}. */
    boolean leaves(V vertex);

    /** Whether it may be gone along to {@code vertex}. */
    boolean enters(V vertex);

    /** Whether {@code vertex} is one of its two. */
    default boolean touches(final V vertex) {
        return one().equals(vertex) || other().equals(vertex);
    }

    /** The vertex at its other end from {@code vertex}, which must be one of its two. */
    default V beyond(final V vertex) {
        if (one().equals(vertex)) {
            return other();
        }
        if (other().equals(vertex)) {
            return one();
        }
        throw new IllegalArgumentException(vertex + " is neither end of " + this);
    }
}

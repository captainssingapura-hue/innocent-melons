package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;
import hue.captains.singapura.tao.ontology.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A graph's edges: each once, in the order they are listed; equal by them and their order. Two edges may join the
 * same two vertices, an arch's curve and its straight chord, so long as they differ, in their way or what they
 * carry. Which of them leave each vertex, which enter it and which touch it, found once.
 *
 * @param <V> what their vertices are
 * @param <L> what they carry
 */
public final class Edges<V extends Scalar, L extends Scalar> implements Vector<Edge<V, L>> {

    private final List<Edge<V, L>> listed;
    private final Map<V, List<Edge<V, L>>> leaving;
    private final Map<V, List<Edge<V, L>>> entering;
    private final Map<V, List<Edge<V, L>>> touching;

    /** {@code listed}, each once. */
    public Edges(final List<? extends Edge<V, L>> listed) {
        this.listed = List.copyOf(listed);
        final Set<Edge<V, L>> seen = new HashSet<>();
        final Map<V, List<Edge<V, L>>> out = new HashMap<>();
        final Map<V, List<Edge<V, L>>> in = new HashMap<>();
        final Map<V, List<Edge<V, L>>> at = new HashMap<>();
        for (final Edge<V, L> edge : this.listed) {
            if (!seen.add(edge)) {
                throw new IllegalArgumentException(edge + " listed twice");
            }
            for (final V end : List.of(edge.one(), edge.other())) {
                at.computeIfAbsent(end, v -> new ArrayList<>()).add(edge);
                if (edge.leaves(end)) {
                    out.computeIfAbsent(end, v -> new ArrayList<>()).add(edge);
                }
                if (edge.enters(end)) {
                    in.computeIfAbsent(end, v -> new ArrayList<>()).add(edge);
                }
            }
        }
        this.leaving = frozen(out);
        this.entering = frozen(in);
        this.touching = frozen(at);
    }

    /** Them, as listed. */
    public List<Edge<V, L>> listed() {
        return listed;
    }

    /** Those that may be gone along from {@code vertex}, as listed. */
    public List<Edge<V, L>> leaving(final V vertex) {
        return leaving.getOrDefault(vertex, List.of());
    }

    /** Those that may be gone along to {@code vertex}, as listed. */
    public List<Edge<V, L>> entering(final V vertex) {
        return entering.getOrDefault(vertex, List.of());
    }

    /** Those with {@code vertex} at either end, as listed. */
    public List<Edge<V, L>> touching(final V vertex) {
        return touching.getOrDefault(vertex, List.of());
    }

    private Map<V, List<Edge<V, L>>> frozen(final Map<V, List<Edge<V, L>>> edges) {
        final Map<V, List<Edge<V, L>>> frozen = new HashMap<>();
        edges.forEach((vertex, those) -> frozen.put(vertex, List.copyOf(those)));
        return Map.copyOf(frozen);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Edges<?, ?> edges && listed.equals(edges.listed);
    }

    @Override
    public int hashCode() {
        return listed.hashCode();
    }

    @Override
    public String toString() {
        return "Edges" + listed;
    }
}

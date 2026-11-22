package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * A graph: its vertices, and its edges between them, each edge's two vertices among its own. What is gone to from a
 * vertex, and what is reached from it; the order its directed edges put its vertices in, each after every vertex it
 * is reached from, if they put them in one; the parts of it that hang together. Each answer is the same every time,
 * its vertices taken in the order they are listed where nothing else orders them.
 *
 * @param vertices its vertices
 * @param edges    its edges
 * @param <V>      what its vertices are
 * @param <L>      what its edges carry
 */
public record Graph<V extends Scalar, L extends Scalar>(Vertices<V> vertices, Edges<V, L> edges) implements Scalar {

    public Graph {
        for (final Edge<V, L> edge : edges.listed()) {
            if (!vertices.has(edge.one()) || !vertices.has(edge.other())) {
                throw new IllegalArgumentException(edge + " joins a vertex the graph does not have");
            }
        }
    }

    /** {@code vertices}, each once, and {@code edges} between them, each once. */
    public Graph(final List<V> vertices, final List<? extends Edge<V, L>> edges) {
        this(new Vertices<>(vertices), new Edges<>(edges));
    }

    /** The vertices gone to in one edge from {@code vertex}, each once, in the order their edges are listed. */
    public List<V> next(final V vertex) {
        return edges.leaving(vertex).stream().map(edge -> edge.beyond(vertex)).distinct().toList();
    }

    /** The vertices reached from {@code vertex} along its edges, it first, nearer before further. */
    public List<V> reached(final V vertex) {
        vertices.place(vertex);
        final Set<V> reached = new LinkedHashSet<>(List.of(vertex));
        final Deque<V> going = new ArrayDeque<>(List.of(vertex));
        while (!going.isEmpty()) {
            for (final V beyond : next(going.removeFirst())) {
                if (reached.add(beyond)) {
                    going.addLast(beyond);
                }
            }
        }
        return List.copyOf(reached);
    }

    /**
     * Its vertices in the order its edges put them, each after every vertex an edge comes to it from: what bears
     * before what it bears on, if the edges all go one way. None if they go round, and an undirected edge goes round,
     * gone along either way. Among vertices that may come next, the one listed first.
     */
    public Optional<List<V>> order() {
        final int[] waiting = new int[vertices.size()];
        final TreeSet<Integer> ready = new TreeSet<>();
        for (final V vertex : vertices.listed()) {
            waiting[vertices.place(vertex)] = edges.entering(vertex).size();
            if (waiting[vertices.place(vertex)] == 0) {
                ready.add(vertices.place(vertex));
            }
        }
        final List<V> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            final V vertex = vertices.listed().get(ready.pollFirst());
            order.add(vertex);
            for (final Edge<V, L> edge : edges.leaving(vertex)) {
                final int beyond = vertices.place(edge.beyond(vertex));
                if (--waiting[beyond] == 0) {
                    ready.add(beyond);
                }
            }
        }
        return order.size() == vertices.size() ? Optional.of(List.copyOf(order)) : Optional.empty();
    }

    /**
     * The parts of it that hang together, whichever way their edges go: each part's vertices as listed, the parts in
     * the order of their first vertices. A vertex no edge touches is a part of its own.
     */
    public List<List<V>> parts() {
        final Set<V> placed = new LinkedHashSet<>();
        final List<List<V>> parts = new ArrayList<>();
        for (final V vertex : vertices.listed()) {
            if (!placed.contains(vertex)) {
                final List<V> part = together(vertex);
                placed.addAll(part);
                parts.add(part);
            }
        }
        return List.copyOf(parts);
    }

    /** The vertices hanging together with {@code vertex}, whichever way their edges go, as listed. */
    private List<V> together(final V vertex) {
        final Set<V> together = new LinkedHashSet<>(List.of(vertex));
        final Deque<V> going = new ArrayDeque<>(List.of(vertex));
        while (!going.isEmpty()) {
            final V at = going.removeFirst();
            for (final Edge<V, L> edge : edges.touching(at)) {
                if (together.add(edge.beyond(at))) {
                    going.addLast(edge.beyond(at));
                }
            }
        }
        return together.stream().sorted(Comparator.comparingInt(vertices::place)).toList();
    }
}

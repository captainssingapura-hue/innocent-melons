package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;
import hue.captains.singapura.tao.ontology.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A graph's vertices: values of one kind, each once, in the order they are listed; equal by them and their order.
 * Two vertices are the same vertex if they are equal: where edges meet is said by the vertices they share.
 *
 * @param <V> what they are
 */
public final class Vertices<V extends Scalar> implements Vector<V> {

    private final List<V> listed;
    private final Map<V, Integer> places;

    /** {@code listed}, each once, none of them nothing. */
    public Vertices(final List<V> listed) {
        this.listed = List.copyOf(listed);
        final Map<V, Integer> at = new HashMap<>();
        for (int i = 0; i < this.listed.size(); i++) {
            if (at.putIfAbsent(this.listed.get(i), i) != null) {
                throw new IllegalArgumentException(this.listed.get(i) + " listed twice");
            }
        }
        this.places = Map.copyOf(at);
    }

    /** Them, as listed. */
    public List<V> listed() {
        return listed;
    }

    /** How many there are. */
    public int size() {
        return listed.size();
    }

    /** Whether {@code vertex} is one of them. */
    public boolean has(final V vertex) {
        return places.containsKey(vertex);
    }

    /** Where {@code vertex}, one of them, is listed, from 0. */
    public int place(final V vertex) {
        final Integer place = places.get(vertex);
        if (place == null) {
            throw new IllegalArgumentException(vertex + " is no vertex of these");
        }
        return place;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Vertices<?> vertices && listed.equals(vertices.listed);
    }

    @Override
    public int hashCode() {
        return listed.hashCode();
    }

    @Override
    public String toString() {
        return "Vertices" + listed;
    }
}

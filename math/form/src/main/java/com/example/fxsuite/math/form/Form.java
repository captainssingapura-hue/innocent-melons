package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import com.example.fxsuite.math.graph.Point.Spatial;
import com.example.fxsuite.math.graph.Undirected;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A form in space as a graph, and nothing more: landmarks, each a named point with no size; edges between them, each
 * running straight or curving as its {@link Course} says, with no thickness; and faces, each a surface spanned by a
 * loop of its edges. What it is made of, how thick its parts are and what they bear are for whatever uses it to say,
 * about its parts by their names: so one shape is the same whether it is drawn, collided with or loaded.
 *
 * <p>From these come where an edge's points are, how long it is and its edges all told, the box it fits in, how many
 * pieces it is in, the surface through a face ({@link Patch}) and what is wrong with it ({@link #problems}). It is
 * kept as text by {@link FormText}.</p>
 *
 * @param name  what it is
 * @param graph its landmarks and its edges
 * @param faces its faces, each along its own edges
 */
public record Form(String name, Graph<Landmark, Course> graph, List<Face> faces) implements Scalar {

    /** Points nearer than this (a millimetre, in centimetres) are the same point. */
    public static final double TOLERANCE = 0.1;
    /** A curved edge is found in this many straight pieces at least. */
    public static final int PIECES = 12;

    public Form {
        faces = List.copyOf(faces);
        final java.util.Set<String> named = new java.util.HashSet<>();
        graph.vertices().listed().forEach(l -> named.add(l.name()));
        final java.util.Set<List<String>> joined = new java.util.HashSet<>();
        for (final Edge<Landmark, Course> e : graph.edges().listed()) {
            joined.add(List.of(e.one().name(), e.other().name()));
            joined.add(List.of(e.other().name(), e.one().name()));
        }
        for (final Face face : faces) {
            for (final List<String> side : face.sides()) {
                for (int i = 0; i < side.size(); i++) {
                    if (!named.contains(side.get(i))) {
                        throw new IllegalArgumentException(face.name() + ": no landmark " + side.get(i));
                    }
                    if (i > 0 && !joined.contains(List.of(side.get(i - 1), side.get(i)))) {
                        throw new IllegalArgumentException(face.name() + ": no edge from " + side.get(i - 1) + " to "
                                + side.get(i));
                    }
                }
            }
        }
    }

    /** Its landmarks and edges, with no faces. */
    public Form(final String name, final Graph<Landmark, Course> graph) {
        this(name, graph, List.of());
    }

    /** Its landmarks, in the order they are listed. */
    public List<Landmark> landmarks() {
        return graph.vertices().listed();
    }

    /** Its edges, in the order they are listed. */
    public List<Edge<Landmark, Course>> edges() {
        return graph.edges().listed();
    }

    /** The landmark named {@code name}. */
    public Landmark landmark(final String name) {
        return find(name).orElseThrow(() -> new IllegalArgumentException(this.name + " has no landmark " + name));
    }

    /** The landmark named {@code name}, if there is one. */
    public Optional<Landmark> find(final String name) {
        return landmarks().stream().filter(l -> l.name().equals(name)).findFirst();
    }

    /** Where the landmark named {@code name} stands. */
    public Spatial at(final String name) {
        return landmark(name).at();
    }

    /** The edge between the landmarks named {@code one} and {@code other}, whichever way it runs, if there is one. */
    public Optional<Edge<Landmark, Course>> edge(final String one, final String other) {
        return edges().stream().filter(e -> e.one().name().equals(one) && e.other().name().equals(other)
                || e.one().name().equals(other) && e.other().name().equals(one)).findFirst();
    }

    /**
     * {@code edge}'s points, end to end from its first landmark: its two ends, if it runs straight; if it curves, so
     * many along its course that no straight piece between them is longer than {@code longest}, {@link #PIECES} at
     * least.
     */
    public List<Spatial> points(final Edge<Landmark, Course> edge, final double longest) {
        final Spatial a = edge.one().at();
        final Spatial b = edge.other().at();
        final Course course = edge.label();
        if (course.straight()) {
            return List.of(a, b);
        }
        double length = 0;
        Spatial last = a;
        for (int k = 1; k <= 64; k++) {
            final Spatial p = course.at(a, b, k / 64.0);
            length += p.distance(last);
            last = p;
        }
        final int pieces = Math.max(PIECES, (int) Math.ceil(length / longest));
        final List<Spatial> points = new ArrayList<>(pieces + 1);
        for (int k = 0; k <= pieces; k++) {
            points.add(k == 0 ? a : k == pieces ? b : course.at(a, b, (double) k / pieces));
        }
        return points;
    }

    /**
     * The run along the landmarks named in {@code path}, in turn, each to the next along the edge between them, its
     * pieces no longer than {@code longest} where it curves; a path of one landmark stays at it.
     */
    public Run run(final List<String> path, final double longest) {
        final List<Spatial> points = new ArrayList<>(List.of(at(path.getFirst())));
        for (int i = 1; i < path.size(); i++) {
            final String from = path.get(i - 1);
            final Edge<Landmark, Course> edge = edge(from, path.get(i)).orElseThrow(() ->
                    new IllegalArgumentException(name + " has no edge from " + from + " on its path"));
            final List<Spatial> along = new ArrayList<>(points(edge, longest));
            if (!edge.one().name().equals(from)) {
                java.util.Collections.reverse(along);
            }
            points.addAll(along.subList(1, along.size()));
        }
        return new Run(points);
    }

    /** How long {@code edge} is: straight, end to end; curved, along its course. */
    public double length(final Edge<Landmark, Course> edge) {
        return new Run(points(edge, 0.5)).length();
    }

    /** Its edges' length, all told. */
    public double length() {
        return edges().stream().mapToDouble(this::length).sum();
    }

    /** The box it fits in, its curved edges and all: its lowest corner, then its highest. */
    public List<Spatial> bounds() {
        final double[] lo = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        final double[] hi = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        final List<Spatial> all = new ArrayList<>(landmarks().stream().map(Landmark::at).toList());
        edges().forEach(e -> all.addAll(points(e, 1)));
        for (final Spatial p : all) {
            for (int axis = 0; axis < 3; axis++) {
                lo[axis] = Math.min(lo[axis], p.coordinate(axis));
                hi[axis] = Math.max(hi[axis], p.coordinate(axis));
            }
        }
        return all.isEmpty() ? List.of() : List.of(new Spatial(lo[0], lo[1], lo[2]), new Spatial(hi[0], hi[1], hi[2]));
    }

    /** How many pieces it is in: landmarks joined by edges, each lot one piece. */
    public int pieces() {
        return graph.parts().size();
    }

    /** The same shape {@code k} times as big every way about the origin, named and joined as it is. */
    public Form scaled(final double k) {
        if (!(k > 0) || Double.isInfinite(k)) {
            throw new IllegalArgumentException(name + " scaled by " + k);
        }
        return moved(l -> new Landmark(l.name(), l.at().times(k)), c -> c.scaled(k));
    }

    /** The same form, each landmark as {@code landmarks} places it and each course as {@code courses} makes it. */
    public Form moved(final java.util.function.UnaryOperator<Landmark> landmarks,
                      final java.util.function.UnaryOperator<Course> courses) {
        final List<Landmark> moved = landmarks().stream().map(landmarks).toList();
        final List<Undirected<Landmark, Course>> edges = edges().stream().map(e -> new Undirected<>(
                moved.get(graph.vertices().place(e.one())), moved.get(graph.vertices().place(e.other())),
                courses.apply(e.label()))).toList();
        return new Form(name, new Graph<>(moved, edges), faces);
    }

    /** What is wrong with it: two landmarks of one name, two so near they are one point, a landmark nothing joins. */
    public List<String> problems() {
        return new Problems(this).found();
    }
}

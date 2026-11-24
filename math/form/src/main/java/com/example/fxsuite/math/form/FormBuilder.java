package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Graph;
import com.example.fxsuite.math.graph.Undirected;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A form put together a landmark, an edge and a face at a time: each landmark by a name of its own, each edge between
 * landmarks by their names, each face by its four sides' names. Made, it is a {@link Form}.
 */
public final class FormBuilder {

    private final String name;
    private final Map<String, Landmark> landmarks = new LinkedHashMap<>();
    private final List<Undirected<Landmark, Course>> edges = new ArrayList<>();
    private final List<Face> faces = new ArrayList<>();

    /** A form to be called {@code name}. */
    public FormBuilder(final String name) {
        this.name = name;
    }

    /** {@code landmark} added; one of the same name already there is a mistake. */
    public FormBuilder landmark(final Landmark landmark) {
        if (landmarks.putIfAbsent(landmark.name(), landmark) != null) {
            throw new IllegalArgumentException(name + " has a landmark called " + landmark.name() + " already");
        }
        return this;
    }

    /** A landmark named {@code called}, at {@code x} across, {@code y} up and {@code z} along. */
    public FormBuilder landmark(final String called, final double x, final double y, final double z) {
        return landmark(new Landmark(called, x, y, z));
    }

    /** A straight edge from the landmark named {@code from} to the one named {@code to}. */
    public FormBuilder edge(final String from, final String to) {
        return edge(from, to, new Course.Straight());
    }

    /** An edge from the landmark named {@code from} to the one named {@code to}, running as {@code course} says. */
    public FormBuilder edge(final String from, final String to, final Course course) {
        edges.add(new Undirected<>(named(from), named(to), course));
        return this;
    }

    /** Straight edges along the landmarks named in {@code path}, each to the next. */
    public FormBuilder path(final String... path) {
        for (int i = 1; i < path.length; i++) {
            edge(path[i - 1], path[i]);
        }
        return this;
    }

    /** A face called {@code called}, its four sides {@code sides} in turn. */
    public FormBuilder face(final String called, final List<List<String>> sides) {
        faces.add(new Face(called, sides));
        return this;
    }

    /** The landmark named {@code called}. */
    public Landmark named(final String called) {
        final Landmark l = landmarks.get(called);
        if (l == null) {
            throw new IllegalArgumentException(name + " has no landmark called " + called);
        }
        return l;
    }

    /** The form as put together. */
    public Form made() {
        return new Form(name, new Graph<>(List.copyOf(landmarks.values()), edges), faces);
    }
}

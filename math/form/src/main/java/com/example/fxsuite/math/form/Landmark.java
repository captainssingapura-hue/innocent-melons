package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Point;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A vertex of a form: a point in space, named. The name is what anything else holds it by; two landmarks are the same
 * landmark only if they are named alike and stand at one point.
 *
 * @param name what it is called, its own in its form
 * @param at   where it stands
 */
public record Landmark(String name, Point.Spatial at) implements Scalar {

    public Landmark {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("a landmark with no name, at " + at);
        }
        if (at == null) {
            throw new IllegalArgumentException("a landmark " + name + " standing nowhere");
        }
    }

    /** Named {@code name}, at {@code x} across, {@code y} up and {@code z} along. */
    public Landmark(final String name, final double x, final double y, final double z) {
        this(name, new Point.Spatial(x, y, z));
    }
}

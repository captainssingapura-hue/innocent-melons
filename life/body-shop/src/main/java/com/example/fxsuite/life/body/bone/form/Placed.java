package com.example.fxsuite.life.body.bone.form;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.math.graph.Point.Spatial;

/**
 * Where a form drawn in its own frame stands in the body's: its origin at {@code origin}, its three ways, across, up
 * and out, the body's ways {@code across}, {@code up} and {@code out}, each a unit long and square to the others. A
 * bone drawn about its own landmarks, as its measures are taken, and then stood where it stands at rest.
 *
 * @param origin where its origin is, in the body
 * @param across its first way, in the body
 * @param up     its second
 * @param out    its third
 */
public record Placed(Spatial origin, Spatial across, Spatial up, Spatial out) {

    public Placed {
        for (final Spatial w : new Spatial[] {across, up, out}) {
            if (Math.abs(w.length() - 1) > 1e-6) {
                throw new IllegalArgumentException("a way of a frame a unit long, not " + w.length());
            }
        }
        if (Math.abs(across.dot(up)) > 1e-6 || Math.abs(up.dot(out)) > 1e-6 || Math.abs(out.dot(across)) > 1e-6) {
            throw new IllegalArgumentException("a frame's ways square to each other");
        }
    }

    /** {@code local}, a point in the form's frame, in the body's. */
    public Spatial point(final Spatial local) {
        return origin.plus(way(local));
    }

    /** {@code local}, a way in the form's frame, in the body's. */
    public Spatial way(final Spatial local) {
        return across.times(local.x()).plus(up.times(local.y())).plus(out.times(local.z()));
    }

    /** {@code local}, a point in the form's frame, in the body's, as the body's sketches take it. */
    public V3 body(final Spatial local) {
        final Spatial p = point(local);
        return new V3(p.x(), p.y(), p.z());
    }

    /** {@code local}, a way in the form's frame, in the body's, as the body's sketches take it. */
    public V3 bodyWay(final Spatial local) {
        final Spatial w = way(local);
        return new V3(w.x(), w.y(), w.z());
    }
}

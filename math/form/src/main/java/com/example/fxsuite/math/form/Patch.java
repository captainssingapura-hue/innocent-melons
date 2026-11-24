package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Point.Spatial;

import java.util.List;

/**
 * The surface through a face of a form: the one that runs along each of its four sides and blends them evenly between
 * (a Coons patch). Across it from its first side to its third is {@code v}, from 0 to 1; along its first side, from
 * its first corner to its second, is {@code u}. At its corners it stands on them; along a side it runs on it, each a
 * share of its own length along; a side of one landmark is a point the surface closes to.
 */
public final class Patch {

    private final Run bottom;
    private final Run right;
    private final Run top;
    private final Run left;
    private final Spatial[] corner;

    /** Through {@code face} of {@code form}, its curved sides found in pieces no longer than {@code longest}. */
    public Patch(final Form form, final Face face, final double longest) {
        final List<List<String>> sides = face.sides();
        bottom = form.run(sides.get(0), longest);
        right = form.run(sides.get(1), longest);
        top = form.run(sides.get(2), longest);
        left = form.run(sides.get(3), longest);
        corner = new Spatial[] {bottom.at(0), right.at(0), top.at(0), left.at(0)};
    }

    /** Where it is, {@code u} along its first side and {@code v} across from it toward its third, both 0 to 1. */
    public Spatial at(final double u, final double v) {
        final Spatial c0 = bottom.at(u);
        final Spatial c1 = top.at(1 - u);
        final Spatial d0 = left.at(1 - v);
        final Spatial d1 = right.at(v);
        // the two rulings, less the bilinear blend of the corners they both hold
        final Spatial ruled = c0.times(1 - v).plus(c1.times(v)).plus(d0.times(1 - u)).plus(d1.times(u));
        final Spatial corners = corner[0].times((1 - u) * (1 - v)).plus(corner[1].times(u * (1 - v)))
                .plus(corner[2].times(u * v)).plus(corner[3].times((1 - u) * v));
        return ruled.minus(corners);
    }

    /** The way square to it at {@code u}, {@code v}: along {@code u} crossed with along {@code v}, a unit long. */
    public Spatial facing(final double u, final double v) {
        final double h = 1e-3;
        final Spatial du = at(Math.min(1, u + h), v).minus(at(Math.max(0, u - h), v));
        final Spatial dv = at(u, Math.min(1, v + h)).minus(at(u, Math.max(0, v - h)));
        return du.cross(dv).unit();
    }
}

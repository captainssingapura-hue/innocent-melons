package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Beneath;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * The point of a triangle nearest a point (Ericson's way): within it, on one of its sides, or at a corner, as the
 * point lies against each; how far, and how much of each corner the nearest point is.
 */
public record Closest() implements StatelessFunctionalObject {

    public static final Closest INSTANCE = new Closest();

    /** The point of {@code b}'s triangle {@code tri} nearest {@code p}. */
    public Nearness.Near on(final Beneath b, final int tri, final V3 p) {
        final int[] t = b.triangles();
        final V3 a = b.point(t[3 * tri]);
        final V3 bb = b.point(t[3 * tri + 1]);
        final V3 c = b.point(t[3 * tri + 2]);
        final double[] w = weights(a, bb, c, p);
        final V3 q = a.times(w[0]).plus(bb.times(w[1])).plus(c.times(w[2]));
        return new Nearness.Near(q.minus(p).length(), tri, w[0], w[1], w[2]);
    }

    /** How much of {@code a}, {@code b} and {@code c} the point of their triangle nearest {@code p} is. */
    double[] weights(final V3 a, final V3 b, final V3 c, final V3 p) {
        final V3 ab = b.minus(a);
        final V3 ac = c.minus(a);
        final V3 ap = p.minus(a);
        final double d1 = ab.dot(ap);
        final double d2 = ac.dot(ap);
        if (d1 <= 0 && d2 <= 0) {
            return new double[] {1, 0, 0};
        }
        final V3 bp = p.minus(b);
        final double d3 = ab.dot(bp);
        final double d4 = ac.dot(bp);
        if (d3 >= 0 && d4 <= d3) {
            return new double[] {0, 1, 0};
        }
        final double vc = d1 * d4 - d3 * d2;
        if (vc <= 0 && d1 >= 0 && d3 <= 0) {
            final double v = d1 / (d1 - d3);
            return new double[] {1 - v, v, 0};
        }
        final V3 cp = p.minus(c);
        final double d5 = ab.dot(cp);
        final double d6 = ac.dot(cp);
        if (d6 >= 0 && d5 <= d6) {
            return new double[] {0, 0, 1};
        }
        final double vb = d5 * d2 - d1 * d6;
        if (vb <= 0 && d2 >= 0 && d6 <= 0) {
            final double w = d2 / (d2 - d6);
            return new double[] {1 - w, 0, w};
        }
        final double va = d3 * d6 - d5 * d4;
        if (va <= 0 && d4 - d3 >= 0 && d5 - d6 >= 0) {
            final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
            return new double[] {0, 1 - w, w};
        }
        final double denom = 1 / (va + vb + vc);
        final double v = vb * denom;
        final double w = vc * denom;
        return new double[] {1 - v - w, v, w};
    }
}

package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How much of a plane a closed surface's inside covers, cm²: a muscle's cross-section, as an image cut across the
 * spine would show it. Each triangle the plane cuts gives a piece of the outline, gone round as the triangle goes
 * round; the outline's pieces, each with the plane's point, make triangles of the plane whose areas, by the way they
 * go round, add up to what the outline closes in, however many pieces of the muscle the plane cuts.
 */
public record CrossSection() implements StatelessFunctionalObject {

    public static final CrossSection INSTANCE = new CrossSection();

    /** How much of the plane through {@code at}, square to {@code up}, the inside of closed {@code surface} covers. */
    public double area(final Mesher.Mesh surface, final V3 at, final V3 up) {
        final V3 n = up.times(1 / up.length());
        final float[] p = surface.points();
        final int[] t = surface.triangles();
        double twice = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3[] c = new V3[3];
            final double[] s = new double[3];
            for (int k = 0; k < 3; k++) {
                c[k] = new V3(p[3 * t[i + k]], p[3 * t[i + k] + 1], p[3 * t[i + k] + 2]);
                s[k] = c[k].minus(at).dot(n);
            }
            V3 from = null;
            V3 to = null;
            for (int k = 0; k < 3; k++) {
                final int j = (k + 1) % 3;
                if ((s[k] < 0) != (s[j] < 0)) {
                    final V3 cut = c[k].plus(c[j].minus(c[k]).times(s[k] / (s[k] - s[j])));
                    // going round the triangle, the edge going up through the plane starts the piece
                    if (s[k] < 0) {
                        from = cut;
                    } else {
                        to = cut;
                    }
                }
            }
            if (from != null && to != null) {
                twice += from.minus(at).cross(to.minus(at)).dot(n);
            }
        }
        return Math.abs(twice) / 2;
    }
}

package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;

/**
 * Which way a surface of triangles faces, and so which side of it a point is on: its points' ways out, each its
 * triangles' summed; a point behind it where those ways, blended where it is nearest, turn from it.
 */
public record Facing() implements StatelessFunctionalObject {

    public static final Facing INSTANCE = new Facing();

    /** How far round toward a limb's line its skin may face, a cosine, before it is taken to be folded over. */
    private static final double BACK = -0.2;

    /** Each of {@code b}'s points' way out, its triangles' summed, each as large as it is: three numbers each. */
    public float[] outward(final Beneath b) {
        final int[] t = b.triangles();
        final float[] out = new float[b.points().length];
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = b.point(t[i]);
            final V3 n = b.point(t[i + 1]).minus(a).cross(b.point(t[i + 2]).minus(a));
            for (int c = 0; c < 3; c++) {
                out[3 * t[i + c]] += (float) n.x();
                out[3 * t[i + c] + 1] += (float) n.y();
                out[3 * t[i + c] + 2] += (float) n.z();
            }
        }
        return out;
    }

    /**
     * Whether {@code p}, nearest {@code m} on {@code b}, is behind it, as its corners' ways out {@code out} blend;
     * where {@code core} says the surface lies round a line and it faces back toward the line, as the way from it.
     */
    public boolean behind(final Beneath b, final float[] out, final Nearness.Near m, final V3 p,
                          final Optional<Plating.Core> core) {
        final int[] t = b.triangles();
        final double[] w = {m.a(), m.b(), m.c()};
        V3 at = V3.ZERO;
        V3 way = V3.ZERO;
        for (int c = 0; c < 3; c++) {
            final int i = t[3 * m.triangle() + c];
            at = at.plus(b.point(i).times(w[c]));
            way = way.plus(new V3(out[3 * i], out[3 * i + 1], out[3 * i + 2]).unit().times(w[c]));
        }
        if (core.isPresent() && b.main()[t[3 * m.triangle()]] == core.get().part()) {
            final V3 from = at.minus(core.get().at());
            final V3 off = from.minus(core.get().way().times(from.dot(core.get().way()))).unit();
            way = way.unit().dot(off) < BACK ? off : way;
        }
        return p.minus(at).dot(way) < 0;
    }
}

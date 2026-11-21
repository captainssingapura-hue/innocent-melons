package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.BoundPose;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A body's muscles as one surface bound to its bones ({@link Flesh}), to be posed as the body moves: every muscle, or
 * all but the deep ones, both sides, as big as they were measured in a body of its height and mass ({@link Measured}),
 * each found as the body is held to be bound, its arms out from its sides ({@link BoundPose}); each point following
 * the parts its muscle's sections' bones move with, as much as it carries of each ({@link MuscleSurface}), the four it
 * follows most; so that a muscle across a joint is drawn out or gathered in as the joint turns. How far each point is
 * tendon kept beside it, to colour it.
 */
public record MuscleBody() implements StatelessFunctionalObject {

    public static final MuscleBody INSTANCE = new MuscleBody();

    /**
     * A body's muscles bound; how far each point of them is tendon; the parts they follow, and the one each point
     * follows most, by its number among them.
     */
    public record Bound(Flesh flesh, float[] tendon, List<Part> parts, int[] main) {
    }

    /** One muscle on one side, to be found. */
    private record Job(Muscle muscle, boolean left) {
    }

    /** The muscles of a body made as {@code p} says, the deep ones if {@code deep}, smoothed {@code steps} times. */
    public Bound make(final Proportions p, final boolean deep, final int steps) {
        final Rig bind = new Rig(p, BoundPose.INSTANCE.pose(), false);
        final Measured bulk = new Measured(p.stature() / 100, p.mass());
        final List<Job> jobs = new ArrayList<>();
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            if (deep || m.layer() != Layer.DEEP) {
                jobs.add(new Job(m, false));
                if (m.paired()) {
                    jobs.add(new Job(m, true));
                }
            }
        }
        final List<MuscleSurface.Surface> found = jobs.parallelStream()
                .map(j -> MuscleSurface.INSTANCE.surface(j.muscle(), bulk, bind, j.left(), steps)).toList();
        return bound(found, bind);
    }

    /** {@code surfaces} put together as one, bound where {@code bind} holds the body. */
    private Bound bound(final List<MuscleSurface.Surface> surfaces, final Rig bind) {
        final List<Part> parts = new ArrayList<>();
        int points = 0;
        int corners = 0;
        for (final MuscleSurface.Surface s : surfaces) {
            points += s.mesh().size();
            corners += s.mesh().triangles().length;
            s.parts().stream().filter(q -> !parts.contains(q)).forEach(parts::add);
        }
        final float[] at = new float[3 * points];
        final int[] triangles = new int[corners];
        final float[] tendon = new float[points];
        final int[] which = new int[Flesh.PARTS * points];
        final float[] shares = new float[Flesh.PARTS * points];
        int p = 0;
        int t = 0;
        for (final MuscleSurface.Surface s : surfaces) {
            System.arraycopy(s.mesh().points(), 0, at, 3 * p, s.mesh().points().length);
            for (final int c : s.mesh().triangles()) {
                triangles[t++] = c + p;
            }
            for (int i = 0; i < s.mesh().size(); i++) {
                tendon[p + i] = s.tendon()[i];
                strongest(s, i, parts, which, shares, p + i);
            }
            p += s.mesh().size();
        }
        final List<Rig.Frame> frames = new ArrayList<>();
        parts.forEach(q -> frames.add(bind.frame(q)));
        final int[] main = new int[points];
        for (int i = 0; i < points; i++) {
            main[i] = Math.max(which[Flesh.PARTS * i], 0);
        }
        return new Bound(new Flesh(at, normals(at, triangles), triangles, parts, frames, which, shares), tendon, parts,
                main);
    }

    /** Point {@code i} of {@code s}: the parts it follows most, at most {@link Flesh#PARTS}, made to add to one. */
    private void strongest(final MuscleSurface.Surface s, final int i, final List<Part> parts, final int[] which,
                           final float[] shares, final int to) {
        final boolean[] taken = new boolean[s.parts().size()];
        float sum = 0;
        for (int k = 0; k < Flesh.PARTS; k++) {
            int best = -1;
            for (int q = 0; q < taken.length; q++) {
                if (!taken[q] && s.shares()[q][i] > 1e-4f && (best < 0 || s.shares()[q][i] > s.shares()[best][i])) {
                    best = q;
                }
            }
            which[Flesh.PARTS * to + k] = best < 0 ? -1 : parts.indexOf(s.parts().get(best));
            shares[Flesh.PARTS * to + k] = best < 0 ? 0 : s.shares()[best][i];
            sum += best < 0 ? 0 : s.shares()[best][i];
            if (best >= 0) {
                taken[best] = true;
            }
        }
        for (int k = 0; k < Flesh.PARTS; k++) {
            shares[Flesh.PARTS * to + k] /= sum;
        }
    }

    /** Each point's way out: its triangles', by their size. */
    private float[] normals(final float[] p, final int[] t) {
        final float[] n = new float[p.length];
        for (int i = 0; i < t.length; i += 3) {
            final int a = 3 * t[i];
            final int b = 3 * t[i + 1];
            final int c = 3 * t[i + 2];
            final float ux = p[b] - p[a];
            final float uy = p[b + 1] - p[a + 1];
            final float uz = p[b + 2] - p[a + 2];
            final float vx = p[c] - p[a];
            final float vy = p[c + 1] - p[a + 1];
            final float vz = p[c + 2] - p[a + 2];
            for (final int q : new int[] {a, b, c}) {
                n[q] += uy * vz - uz * vy;
                n[q + 1] += uz * vx - ux * vz;
                n[q + 2] += ux * vy - uy * vx;
            }
        }
        for (int i = 0; i < n.length; i += 3) {
            final float l = (float) Math.sqrt(n[i] * n[i] + n[i + 1] * n[i + 1] + n[i + 2] * n[i + 2]);
            final float k = l > 0 ? 1 / l : 0;
            n[i] *= k;
            n[i + 1] *= k;
            n[i + 2] *= k;
        }
        return n;
    }
}

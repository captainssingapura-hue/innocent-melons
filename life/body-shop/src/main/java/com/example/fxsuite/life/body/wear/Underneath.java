package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * What a garment is fitted over, put together ({@link Beneath}): a body's muscles as they are bound
 * ({@link MuscleBody}), each point following its parts as the muscles' do; and its bones as finely as asked, each
 * point following its own part alone, where they show between the muscles, the shins' fronts, the knees, the ankles,
 * the feet; and anything else bound to the body over them, a garment worn under, one more layer.
 */
public record Underneath() implements StatelessFunctionalObject {

    public static final Underneath INSTANCE = new Underneath();

    /** {@code body}'s muscles and its bones, as finely as {@code detail}, where {@code bind} holds them. */
    public Beneath of(final MuscleBody.Bound body, final Rig bind, final Detail detail) {
        return with(muscles(body.flesh()), bones(bind, detail));
    }

    /** What {@code flesh} covers, as bound, each point following its parts as it does. */
    public Beneath muscles(final Flesh flesh) {
        final float[] at = new float[3 * flesh.size()];
        final Part[] main = new Part[flesh.size()];
        final Weights[] w = new Weights[flesh.size()];
        final int[] which = flesh.which();
        final float[] shares = flesh.shares();
        for (int i = 0; i < flesh.size(); i++) {
            final V3 p = flesh.point(i);
            at[3 * i] = (float) p.x();
            at[3 * i + 1] = (float) p.y();
            at[3 * i + 2] = (float) p.z();
            w[i] = gathered(flesh.parts(), which, shares, Flesh.PARTS * i);
            main[i] = w[i].most();
        }
        return new Beneath(at, flesh.triangles(), main, w);
    }

    /** As much as {@code which} and {@code shares} say from {@code at}, {@link Flesh#PARTS} of them, of {@code parts}. */
    private Weights gathered(final List<Part> parts, final int[] which, final float[] shares, final int at) {
        final float[] w = new float[Part.values().length];
        for (int k = 0; k < Flesh.PARTS; k++) {
            final int q = which[at + k];
            if (q >= 0) {
                w[parts.get(q).ordinal()] += shares[at + k];
            }
        }
        return new Weights(w);
    }

    /** Every part's bones as finely as {@code detail}, where {@code bind} holds them, each following its own part. */
    public Beneath bones(final Rig bind, final Detail detail) {
        final List<V3> at = new ArrayList<>();
        final List<Integer> tri = new ArrayList<>();
        final List<Part> by = new ArrayList<>();
        for (final Part p : Part.values()) {
            final Rig.Frame f = bind.frame(p);
            for (final PartBones.Piece piece : PartBones.INSTANCE.pieces(p.side() < 0 ? p.other() : p, detail)) {
                final int from = at.size();
                final float[] q = piece.mesh().points();
                for (int i = 0; i < q.length; i += 3) {
                    at.add(f.place(new V3(q[i], q[i + 1], q[i + 2])));
                    by.add(p);
                }
                for (final int c : piece.mesh().triangles()) {
                    tri.add(from + c);
                }
            }
        }
        final float[] points = new float[3 * at.size()];
        final Weights[] w = new Weights[at.size()];
        for (int i = 0; i < at.size(); i++) {
            points[3 * i] = (float) at.get(i).x();
            points[3 * i + 1] = (float) at.get(i).y();
            points[3 * i + 2] = (float) at.get(i).z();
            final float[] alone = new float[Part.values().length];
            alone[by.get(i).ordinal()] = 1;
            w[i] = new Weights(alone);
        }
        return new Beneath(points, tri.stream().mapToInt(Integer::intValue).toArray(), by.toArray(new Part[0]), w);
    }

    /** {@code a} and {@code b} together, one surface. */
    public Beneath with(final Beneath a, final Beneath b) {
        final float[] points = new float[a.points().length + b.points().length];
        System.arraycopy(a.points(), 0, points, 0, a.points().length);
        System.arraycopy(b.points(), 0, points, a.points().length, b.points().length);
        final int[] tri = new int[a.triangles().length + b.triangles().length];
        System.arraycopy(a.triangles(), 0, tri, 0, a.triangles().length);
        for (int i = 0; i < b.triangles().length; i++) {
            tri[a.triangles().length + i] = b.triangles()[i] + a.size();
        }
        final Part[] main = new Part[a.size() + b.size()];
        System.arraycopy(a.main(), 0, main, 0, a.size());
        System.arraycopy(b.main(), 0, main, a.size(), b.size());
        final Weights[] w = new Weights[a.size() + b.size()];
        System.arraycopy(a.weights(), 0, w, 0, a.size());
        System.arraycopy(b.weights(), 0, w, a.size(), b.size());
        return new Beneath(points, tri, main, w);
    }
}

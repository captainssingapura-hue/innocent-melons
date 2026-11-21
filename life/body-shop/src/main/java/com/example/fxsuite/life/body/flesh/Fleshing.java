package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Dof;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Moved;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Stack;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A body's flesh made one surface ({@link Flesh}): held to be bound, its arms out from its sides and its legs a little
 * apart, so that neither runs into the trunk, it is run through in chains of the parts a tube runs on, the trunk from
 * the hips to the neck, each leg, each arm, each chain's rings, its parts' together, one shape of rings stacked
 * ({@link Stack}), smooth from one part to the next; the chains joined, blended into each other where they meet, at the
 * armpits, the groin, the shoulders; that one shape's surface found. Each point of it follows the part whose bone is
 * nearest it on the chain it is nearest, shading into the next across each joint near it ({@link Binding}).
 */
public record Fleshing() implements StatelessFunctionalObject {

    public static final Fleshing INSTANCE = new Fleshing();

    /** How far round the chains blend into each other where they meet, cm. */
    private static final double BLEND = 3.0;
    /** A chain: its parts in order along it, where its first was bound, its shape in that frame. */
    private record Chain(String said, List<Part> parts, Rig.Frame frame, Moved shape) {
    }

    /** How the body is held to be bound: every part unturned, but the arms out 35 degrees and the legs 5. */
    public Pose bind() {
        return Pose.REST.unturned().with(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 35)
                .with(Part.LEFT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 35).with(Part.RIGHT_THIGH, Dof.HIP_SIDEWAYS, 5)
                .with(Part.LEFT_THIGH, Dof.HIP_SIDEWAYS, 5);
    }

    /** {@code physique}'s flesh, one surface, as finely as {@code detail} says, its muscles as its torso's are. */
    public Flesh make(final Physique physique, final Detail detail) {
        return make(physique, detail, physique.torso().definition());
    }

    /**
     * {@code physique}'s flesh, one surface, as finely as {@code detail} says, its torso's muscles as defined as
     * {@code definition} says; the front of its torso, from the crotch to the armpits, twice as finely.
     */
    public Flesh make(final Physique physique, final Detail detail, final double definition) {
        final Rig bind = new Rig(physique.proportions(), bind(), false);
        final double cell = detail.cell();
        final double scale = physique.proportions().scale();
        final List<Chain> chains = new ArrayList<>();
        final Sculptor.Rows torso = TorsoShape.INSTANCE.rows(physique.torso().defined(definition), physique.female(),
                scale, cell / 2);
        chains.add(chainOf("trunk", List.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.NECK), bind, torso));
        for (final boolean right : new boolean[] {true, false}) {
            final Part thigh = right ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part arm = right ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
            chains.add(chain(thigh.said(), List.of(thigh, right ? Part.RIGHT_SHIN : Part.LEFT_SHIN), physique, bind,
                    cell));
            chains.add(chain(arm.said(), List.of(arm, right ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM), physique, bind,
                    cell));
        }
        final List<Field.Element> elements = new ArrayList<>();
        for (final Chain c : chains) {
            elements.add(new Field.Element(c.said(), c.shape(), Field.Op.ADD, BLEND));
        }
        final Field field = new Field(elements);
        final Rig drawn = new Rig(com.example.fxsuite.life.body.Proportions.DRAWN, bind(), false);
        final double low = (Landmark.CROTCH.y(drawn) - 1) * scale;
        final double high = Landmark.ARMPIT.y(drawn) * scale;
        return bound(Refiner.INSTANCE.refined(Mesher.of(field, cell), field,
                v -> v.z() > 1.5 * scale && Math.abs(v.x()) < 16 * scale && v.y() > low && v.y() < high), chains, bind,
                new Binding(bind, scale));
    }

    /** The chain through {@code parts}, its tubes' rings together, bound where {@code bind} holds them. */
    private Chain chain(final String said, final List<Part> parts, final Physique physique, final Rig bind,
                        final double cell) {
        final Rig.Frame f = bind.frame(parts.getFirst());
        final List<V3> middles = new ArrayList<>();
        final List<V3[]> ends = new ArrayList<>();
        for (final Part p : parts) {
            final Sculptor.Outline o = Sculptor.INSTANCE.outline(physique.on(p).orElseThrow(), physique.female());
            final Rig.Frame pf = bind.frame(p);
            for (int i = 0; i < o.middles().size(); i++) {
                final V3[] e = new V3[o.ends().get(i).length];
                for (int k = 0; k < e.length; k++) {
                    e[k] = inChain(f, pf.place(o.ends().get(i)[k]));
                }
                middles.add(inChain(f, pf.place(o.middles().get(i))));
                ends.add(e);
            }
        }
        return chainOf(said, parts, bind, Sculptor.INSTANCE.rows(sorted(middles, ends), cell));
    }

    /** The chain through {@code parts}, its shape {@code rows} in the frame its first part is bound in. */
    private Chain chainOf(final String said, final List<Part> parts, final Rig bind, final Sculptor.Rows rows) {
        final Rig.Frame f = bind.frame(parts.getFirst());
        return new Chain(said, parts, f, new Moved(new Stack(rows.rows(), rows.middles()), f.turn(), f.at(), false, 0));
    }

    /** {@code v}, a point in the body, in the frame of {@code f}, turned and moved but not scaled. */
    private V3 inChain(final Rig.Frame f, final V3 v) {
        return f.turn().back().apply(v.minus(f.at()));
    }

    /** The rings top to bottom, a ring two parts share (one where another is) once. */
    private Sculptor.Outline sorted(final List<V3> middles, final List<V3[]> ends) {
        final List<Integer> order = new ArrayList<>();
        for (int i = 0; i < middles.size(); i++) {
            order.add(i);
        }
        order.sort((a, b) -> Double.compare(middles.get(b).y(), middles.get(a).y()));
        final List<V3> m = new ArrayList<>();
        final List<V3[]> e = new ArrayList<>();
        for (final int i : order) {
            if (m.isEmpty() || m.getLast().minus(middles.get(i)).length() > 0.3) {
                m.add(middles.get(i));
                e.add(ends.get(i));
            }
        }
        return new Sculptor.Outline(m, e);
    }

    /** {@code mesh} bound to its chains' parts: each point to its nearest chain's, shared at joints. */
    private Flesh bound(final Mesher.Mesh mesh, final List<Chain> chains, final Rig bind, final Binding binding) {
        final List<Part> parts = new ArrayList<>();
        chains.forEach(c -> parts.addAll(c.parts()));
        final float[] p = mesh.points();
        final int n = p.length / 3;
        final int[] which = new int[n * Flesh.PARTS];
        final float[] shares = new float[n * Flesh.PARTS];
        final double[] w = new double[parts.size()];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(w, 0);
            final V3 v = new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2]);
            binding.shares(v, nearest(v, chains).parts(), parts, w);
            strongest(w, i, which, shares);
        }
        final List<Rig.Frame> frames = new ArrayList<>();
        parts.forEach(q -> frames.add(bind.frame(q)));
        return new Flesh(p, normals(p, mesh.triangles()), mesh.triangles(), parts, frames, which, shares);
    }

    /** The chain {@code v} is nearest, or most within. */
    private Chain nearest(final V3 v, final List<Chain> chains) {
        Chain best = chains.getFirst();
        double least = Double.MAX_VALUE;
        for (final Chain c : chains) {
            final double d = c.shape().at(v.x(), v.y(), v.z());
            if (d < least) {
                least = d;
                best = c;
            }
        }
        return best;
    }

    /** Point {@code i}'s {@link Flesh#PARTS} strongest shares of {@code w}, made to add to one. */
    private void strongest(final double[] w, final int i, final int[] which, final float[] shares) {
        double sum = 0;
        for (int k = 0; k < Flesh.PARTS; k++) {
            int best = -1;
            for (int q = 0; q < w.length; q++) {
                if (w[q] > 1e-3 && (best < 0 || w[q] > w[best])) {
                    best = q;
                }
            }
            which[Flesh.PARTS * i + k] = best;
            shares[Flesh.PARTS * i + k] = best < 0 ? 0 : (float) w[best];
            sum += best < 0 ? 0 : w[best];
            if (best >= 0) {
                w[best] = 0;
            }
        }
        for (int k = 0; k < Flesh.PARTS; k++) {
            shares[Flesh.PARTS * i + k] /= (float) sum;
        }
    }

    /** Each point's way out: its triangles', by their size. */
    private float[] normals(final float[] p, final int[] t) {
        final float[] n = new float[p.length];
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            final V3 f = b.minus(a).cross(c.minus(a));
            for (int k = 0; k < 3; k++) {
                n[3 * t[i + k]] += (float) f.x();
                n[3 * t[i + k] + 1] += (float) f.y();
                n[3 * t[i + k] + 2] += (float) f.z();
            }
        }
        for (int i = 0; i < n.length; i += 3) {
            final double l = Math.sqrt(n[i] * n[i] + n[i + 1] * n[i + 1] + n[i + 2] * n[i + 2]);
            for (int k = 0; k < 3; k++) {
                n[i + k] = (float) (l > 0 ? n[i + k] / l : 0);
            }
        }
        return n;
    }
}

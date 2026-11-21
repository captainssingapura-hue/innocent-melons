package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cloth cut from a skin ({@link Flesh}): tights, every bit of the skin below their waistband but the arms', the
 * triangles across the waistband cut along it so that its edge is straight; stood off the skin as thick as the cloth,
 * the band a little more; following the parts the skin there follows, as much, so that it moves as the skin does. The
 * skin wholly under it left out, not to show through.
 */
public record Tailor() implements StatelessFunctionalObject {

    public static final Tailor INSTANCE = new Tailor();

    /** A skin and what is cut from it to wear: what is left bare, and what is worn. */
    public record Clad(Flesh bare, Flesh worn) {
    }

    /** One point of the cloth: the skin's point {@code a}, or {@code t} of the way from it to {@code b}. */
    private record Ref(int a, int b, double t) {
    }

    /** {@code skin} in {@code tights}, its body made {@code scale} times the drawn figure. */
    public Clad clad(final Flesh skin, final Tights tights, final double scale) {
        final Rig drawn = new Rig(Proportions.DRAWN, Fleshing.INSTANCE.bind(), false);
        final double top = (tights.waist().y(drawn) + tights.off()) * scale;
        final boolean[] in = new boolean[skin.size()];
        for (int i = 0; i < in.length; i++) {
            final double arms = skin.share(i, Part.RIGHT_UPPER_ARM) + skin.share(i, Part.RIGHT_FOREARM)
                    + skin.share(i, Part.LEFT_UPPER_ARM) + skin.share(i, Part.LEFT_FOREARM);
            in[i] = skin.point(i).y() < top && arms < 0.5;
        }
        final int[] t = skin.triangles();
        final List<Integer> bare = new ArrayList<>();
        final List<Ref[]> worn = new ArrayList<>();
        for (int i = 0; i < t.length; i += 3) {
            if (in[t[i]] && in[t[i + 1]] && in[t[i + 2]]) {
                worn.add(new Ref[] {new Ref(t[i], -1, 0), new Ref(t[i + 1], -1, 0), new Ref(t[i + 2], -1, 0)});
            } else {
                bare.addAll(List.of(t[i], t[i + 1], t[i + 2]));
                if (in[t[i]] || in[t[i + 1]] || in[t[i + 2]]) {
                    worn.addAll(below(skin, new int[] {t[i], t[i + 1], t[i + 2]}, top));
                }
            }
        }
        final Flesh left = new Flesh(points(skin), skin.normals(), bare.stream().mapToInt(Integer::intValue).toArray(),
                skin.parts(), skin.bound(), skin.which(), skin.shares());
        return new Clad(left, sewn(skin, worn, tights, top, scale));
    }

    /** Triangle {@code v} cut along the waistband at {@code top}: what of it lies below, as triangles. */
    private List<Ref[]> below(final Flesh skin, final int[] v, final double top) {
        final List<Ref> poly = new ArrayList<>();
        for (int k = 0; k < 3; k++) {
            final int a = v[k];
            final int b = v[(k + 1) % 3];
            final double da = skin.point(a).y() - top;
            final double db = skin.point(b).y() - top;
            if (da < 0) {
                poly.add(new Ref(a, -1, 0));
            }
            if (da < 0 != db < 0) {
                poly.add(new Ref(a, b, da / (da - db)));
            }
        }
        final List<Ref[]> out = new ArrayList<>();
        for (int k = 1; k + 1 < poly.size(); k++) {
            out.add(new Ref[] {poly.getFirst(), poly.get(k), poly.get(k + 1)});
        }
        return out;
    }

    /** The cloth: its triangles' points made, stood off the skin, each following the parts it lies over. */
    private Flesh sewn(final Flesh skin, final List<Ref[]> triangles, final Tights tights, final double top,
                       final double scale) {
        final float[] n = skin.normals();
        final int[] which = skin.which();
        final float[] shares = skin.shares();
        final Map<Ref, Integer> made = new HashMap<>();
        final List<float[]> points = new ArrayList<>();
        final List<float[]> ways = new ArrayList<>();
        final List<int[]> whose = new ArrayList<>();
        final List<float[]> much = new ArrayList<>();
        final int[] tri = new int[triangles.size() * 3];
        int k = 0;
        for (final Ref[] tr : triangles) {
            for (final Ref r : tr) {
                tri[k++] = made.computeIfAbsent(r, x -> {
                    final V3 at = lerp(skin.point(x.a()), x.b() < 0 ? skin.point(x.a()) : skin.point(x.b()), x.t());
                    final V3 nb = x.b() < 0 ? normal(n, x.a()) : normal(n, x.b());
                    final V3 out = lerp(normal(n, x.a()), nb, x.t()).unit();
                    final double thick = tights.thickness() * scale
                            + (at.y() > top - tights.bandDeep() * scale ? tights.band() * scale : 0);
                    final V3 p = at.plus(out.times(thick));
                    points.add(new float[] {(float) p.x(), (float) p.y(), (float) p.z()});
                    ways.add(new float[] {(float) out.x(), (float) out.y(), (float) out.z()});
                    final Map<Integer, Double> follow = new HashMap<>();
                    follows(which, shares, x.a(), 1 - x.t(), follow);
                    if (x.b() >= 0) {
                        follows(which, shares, x.b(), x.t(), follow);
                    }
                    strongest(follow, whose, much);
                    return points.size() - 1;
                });
            }
        }
        return new Flesh(flat(points), flat(ways), tri, skin.parts(), skin.bound(), flatInts(whose), flat(much));
    }

    /** What point {@code i} follows, {@code w} of it, added into {@code into}. */
    private void follows(final int[] which, final float[] shares, final int i, final double w,
                         final Map<Integer, Double> into) {
        for (int k = 0; k < Flesh.PARTS; k++) {
            final int q = which[Flesh.PARTS * i + k];
            if (q >= 0) {
                into.merge(q, w * shares[Flesh.PARTS * i + k], Double::sum);
            }
        }
    }

    /** The strongest {@link Flesh#PARTS} of {@code follow}, made to add to one, onto {@code whose} and {@code much}. */
    private void strongest(final Map<Integer, Double> follow, final List<int[]> whose, final List<float[]> much) {
        final List<Map.Entry<Integer, Double>> by = new ArrayList<>(follow.entrySet());
        by.sort((x, y) -> Double.compare(y.getValue(), x.getValue()));
        final int[] w = {-1, -1, -1, -1};
        final float[] s = new float[Flesh.PARTS];
        double sum = 0;
        for (int k = 0; k < Math.min(Flesh.PARTS, by.size()); k++) {
            w[k] = by.get(k).getKey();
            s[k] = by.get(k).getValue().floatValue();
            sum += s[k];
        }
        for (int k = 0; k < Flesh.PARTS; k++) {
            s[k] /= (float) sum;
        }
        whose.add(w);
        much.add(s);
    }

    private float[] points(final Flesh skin) {
        final float[] p = new float[skin.size() * 3];
        for (int i = 0; i < skin.size(); i++) {
            final V3 v = skin.point(i);
            p[3 * i] = (float) v.x();
            p[3 * i + 1] = (float) v.y();
            p[3 * i + 2] = (float) v.z();
        }
        return p;
    }

    private V3 normal(final float[] n, final int i) {
        return new V3(n[3 * i], n[3 * i + 1], n[3 * i + 2]);
    }

    private V3 lerp(final V3 a, final V3 b, final double t) {
        return a.plus(b.minus(a).times(t));
    }

    private float[] flat(final List<float[]> rows) {
        final int w = rows.isEmpty() ? 0 : rows.getFirst().length;
        final float[] out = new float[rows.size() * w];
        for (int i = 0; i < rows.size(); i++) {
            System.arraycopy(rows.get(i), 0, out, i * w, w);
        }
        return out;
    }

    private int[] flatInts(final List<int[]> rows) {
        final int[] out = new int[rows.size() * Flesh.PARTS];
        for (int i = 0; i < rows.size(); i++) {
            System.arraycopy(rows.get(i), 0, out, i * Flesh.PARTS, Flesh.PARTS);
        }
        return out;
    }
}

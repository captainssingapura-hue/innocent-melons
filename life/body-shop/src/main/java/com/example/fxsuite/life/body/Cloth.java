package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * What clothes are made of, as numbers, from the body they are on: a garment fitted to a part as its skin pushed out
 * by how loose it is; a sleeve flaring from the elbow; a band laid along the body, as a sash or a strap is; and a skirt
 * hanging from the waist, its rings level as cloth hangs, every one wide enough to clear the legs inside it wherever
 * they swing. Nothing stored: each worked out from the body as it is made or as it moves.
 */
public final class Cloth {

    private Cloth() {
    }

    /**
     * {@code skin} made a garment: each of its rings pushed out from its own middle by {@code ease}, its ends out along
     * it as far; called {@code name}, coloured {@code paint}.
     */
    public static Surface fitted(final Surface skin, final double ease, final String name, final Paint paint) {
        final List<List<V3>> rings = new ArrayList<>();
        final List<V3> middles = new ArrayList<>();
        for (final List<V3> ring : skin.rings()) {
            V3 c = V3.ZERO;
            for (final V3 p : ring) {
                c = c.plus(p);
            }
            c = c.times(1.0 / ring.size());
            middles.add(c);
            final List<V3> out = new ArrayList<>(ring.size());
            for (final V3 p : ring) {
                final V3 d = p.minus(c);
                out.add(d.length() < 1e-9 ? p : p.plus(d.unit().times(ease)));
            }
            rings.add(out);
        }
        final V3 start = away(skin.start(), middles.getFirst(), ease);
        final V3 end = away(skin.end(), middles.getLast(), ease);
        return new Surface(name, paint, rings, start, end, skin.smooth());
    }

    private static V3 away(final V3 p, final V3 from, final double by) {
        final V3 d = p.minus(from);
        return d.length() < 1e-9 ? p : p.plus(d.unit().times(by));
    }

    /**
     * A sleeve flaring from {@code top} to {@code bottom}, as wide round as {@code from} there and {@code to} at its
     * end, a little flatter front to back; a hem round its end.
     */
    public static List<Surface> bell(final String name, final Paint paint, final Paint hem, final V3 top,
                                     final V3 bottom, final double from, final double to) {
        final int n = 6;
        final double[] radii = new double[n];
        for (int k = 0; k < n; k++) {
            final double t = (double) k / (n - 1);
            radii[k] = from + (to - from) * t * t;
        }
        final V3 u = bottom.minus(top).unit();
        return List.of(Shapes.capsule(name, paint, top, bottom, radii, 0.85, 0.15, 24),
                Shapes.capsule(name + " hem", hem, bottom.minus(u.times(1.2)), bottom.plus(u.times(0.4)),
                        new double[] {to + 0.5, to + 0.5}, 0.85, 0.1, 24));
    }

    /**
     * A flat band through {@code way}, lying on the body round {@code middle}: {@code wide} across it, {@code thick}
     * off it, piece by piece.
     */
    public static List<Surface> band(final String name, final Paint paint, final List<V3> way, final V3 middle,
                                     final double wide, final double thick) {
        final List<Surface> band = new ArrayList<>();
        for (int k = 0; k + 1 < way.size(); k++) {
            final V3 a = way.get(k);
            final V3 b = way.get(k + 1);
            // flat on the body: wide across it, square to the way it runs
            final V3 out = a.plus(b).times(0.5).minus(middle);
            final V3 across = out.cross(b.minus(a));
            // a little past each end, so the pieces meet without a gap
            final V3 u = b.minus(a).unit().times(Math.min(wide * 0.2, 2));
            band.add(Shapes.bar(name + " " + k, paint, a.minus(u), b.plus(u), across, wide, thick));
        }
        return band;
    }

    /** A leg as the skirt sees it: from its hip down to its knee and its ankle, so thick at each, cm. */
    public record Leg(V3 hip, V3 knee, V3 ankle, double atHip, double atKnee, double atAnkle) {

        /** Where the leg is, and how thick, at {@code y} up: null if it does not reach so high or low. */
        double[] at(final double y) {
            final double[] upper = cut(hip, knee, atHip, atKnee, y);
            return upper != null ? upper : cut(knee, ankle, atKnee, atAnkle, y);
        }

        private static double[] cut(final V3 a, final V3 b, final double ra, final double rb, final double y) {
            final double lo = Math.min(a.y(), b.y());
            final double hi = Math.max(a.y(), b.y());
            if (y < lo || y > hi || hi - lo < 1e-6) {
                return null;
            }
            final double t = (a.y() - y) / (a.y() - b.y());
            return new double[] {a.x() + (b.x() - a.x()) * t, a.z() + (b.z() - a.z()) * t, ra + (rb - ra) * t};
        }
    }

    /**
     * A skirt hanging from {@code waist} (its middle, {@code waistAcross} out to each side and {@code waistDeep} to the
     * front and back) down to {@code hem} over the ground, flaring to {@code hemAcross} and {@code hemDeep}: its rings
     * level, each one at least as wide as the one above it and wide enough, {@code ease} to spare, to hold
     * {@code legs} where they are; soft folds round it, deeper toward its hem.
     */
    public static Surface skirt(final String name, final Paint paint, final V3 waist, final double waistAcross,
                                final double waistDeep, final double hem, final double hemAcross, final double hemDeep,
                                final List<Leg> legs, final double ease, final int folds) {
        final int rings = 12;
        final int round = 40;
        final List<List<V3>> out = new ArrayList<>();
        final double cx = waist.x();
        final double cz = waist.z();
        // how far out it reaches each way from the waist's middle: to the right and left, to the front and back
        double right = waistAcross;
        double left = waistAcross;
        double front = waistDeep;
        double back = waistDeep;
        for (int k = 0; k < rings; k++) {
            final double t = (double) k / (rings - 1);
            // more rings near the waist, where it turns from fitted to hanging
            final double s = t * t * 0.4 + t * 0.6;
            final double y = waist.y() + (hem - waist.y()) * s;
            final double across = waistAcross + (hemAcross - waistAcross) * s;
            final double deep = waistDeep + (hemDeep - waistDeep) * s;
            // no narrower than the ring above, nor than it flares; and out as far as each leg needs, each way alone:
            // the leg ahead pushes its front out, the one behind its back
            right = Math.max(right, across);
            left = Math.max(left, across);
            front = Math.max(front, deep);
            back = Math.max(back, deep);
            for (final Leg leg : legs) {
                final double[] l = leg.at(y);
                if (l == null) {
                    continue;
                }
                right = Math.max(right, l[0] - cx + l[2] + ease);
                left = Math.max(left, cx - l[0] + l[2] + ease);
                front = Math.max(front, l[1] - cz + l[2] + ease);
                back = Math.max(back, cz - l[1] + l[2] + ease);
            }
            final List<V3> ring = new ArrayList<>(round);
            for (int j = 0; j < round; j++) {
                final double phi = 2 * Math.PI * j / round;
                final double c = Math.cos(phi);
                final double sn = Math.sin(phi);
                final double fold = 1 + 0.035 * s * Math.sin(folds * phi);
                ring.add(new V3(cx + (c > 0 ? right : left) * fold * c, y, cz + (sn > 0 ? front : back) * fold * sn));
            }
            out.add(ring);
        }
        final V3 last = out.getLast().getFirst();
        return new Surface(name, paint, out, new V3(cx, waist.y() + 1, cz), new V3(cx, last.y(), cz), true);
    }
}

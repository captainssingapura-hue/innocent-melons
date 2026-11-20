package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A garment fitted to a body's contour, all of a piece, and moving with it: made at rest, each of its points out from
 * the body as far as the body's outline is ({@link Envelope}), and its ease; its hollows bridged, as cloth bridges
 * them. Each point tied to the parts it lies over, by weight: on the chest to the chest, over the top of the shoulder
 * half to the chest and half to the arm, down the sleeve to the arm; each frame, where those parts carry it, blended
 * (skinning), so the cloth over a joint bends with it rather than parting or piling up.
 *
 * <p>For now, the upper part of a robe: its body from the waist to round the neck, and its sleeves from the shoulders
 * to the elbows ({@link #robe}).
 */
public final class Garment {

    /** A point of a garment: where it is at rest, in the body's frame, and the parts it moves with, by weight. */
    public record Bound(V3 rest, Part[] parts, double[] weights) {
    }

    /** A piece of it: rings of points round it, closed at each end, coloured {@code paint}. */
    public record Piece(String name, Paint paint, List<List<Bound>> rings, Bound start, Bound end) {
    }

    private final Rig rest;
    private final List<Piece> pieces;

    private Garment(final Rig rest, final List<Piece> pieces) {
        this.rest = rest;
        this.pieces = pieces;
    }

    /** Its pieces, the robe's body first. */
    public List<Piece> pieces() {
        return pieces;
    }

    /** Every piece as {@code now} carries it, in the body's frame. */
    public List<Surface> at(final Rig now) {
        final List<Surface> out = new ArrayList<>();
        for (final Piece p : pieces) {
            final List<List<V3>> rings = new ArrayList<>();
            for (final List<Bound> ring : p.rings()) {
                rings.add(ring.stream().map(b -> at(b, now)).toList());
            }
            out.add(new Surface(p.name(), p.paint(), rings, at(p.start(), now), at(p.end(), now), true));
        }
        return out;
    }

    /** Its body alone, without its sleeves, as {@code now} carries it: a vest's. */
    public Surface bodyAt(final Rig now) {
        return at(now).getFirst();
    }

    /**
     * A point on its body as {@link #on} finds one, but {@code high} of the way up from its waist to its neck by height,
     * not by its rings.
     */
    public Bound onHeight(final double high, final double round, final double off) {
        final List<List<Bound>> rings = pieces.getFirst().rings();
        final double bottom = rings.getFirst().getFirst().rest().y();
        final double top = rings.getLast().getFirst().rest().y();
        final double y = bottom + (top - bottom) * Math.clamp(high, 0, 1);
        int best = 0;
        for (int i = 0; i < rings.size(); i++) {
            if (Math.abs(rings.get(i).getFirst().rest().y() - y) < Math.abs(rings.get(best).getFirst().rest().y() - y)) {
                best = i;
            }
        }
        return on((double) best / (rings.size() - 1), round, off);
    }

    /** Where {@code b} is as {@code now} carries it: where each of its parts takes it, blended by weight. */
    public V3 at(final Bound b, final Rig now) {
        V3 sum = V3.ZERO;
        for (int k = 0; k < b.parts().length; k++) {
            final Part p = b.parts()[k];
            sum = sum.plus(now.frame(p).place(rest.frame(p).local(b.rest())).times(b.weights()[k]));
        }
        return sum;
    }

    /**
     * A point on the robe's body, {@code up} of the way from its waist to its neck and {@code round} degrees round
     * from its right (90 its front), {@code off} out from it: to lay a collar or a sash on it, moving as it does.
     */
    public Bound on(final double up, final double round, final double off) {
        final Piece body = pieces.getFirst();
        final int i = (int) Math.round(Math.clamp(up, 0, 1) * (body.rings().size() - 1));
        final List<Bound> ring = body.rings().get(i);
        final int n = ring.size();
        final int j = Math.floorMod((int) Math.round(round / 360.0 * n), n);
        final Bound b = ring.get(j);
        V3 middle = V3.ZERO;
        for (final Bound r : ring) {
            middle = middle.plus(r.rest());
        }
        middle = middle.times(1.0 / n);
        final V3 out = new V3(b.rest().x() - middle.x(), 0, b.rest().z() - middle.z());
        final V3 at = out.length() < 1e-9 ? b.rest() : b.rest().plus(out.unit().times(off));
        return new Bound(at, b.parts(), b.weights());
    }

    /** How many rings round the robe's body, and points round each; round each sleeve, and along it. */
    static final int BODY_RINGS = 34;
    static final int BODY_ROUND = 48;
    static final int SLEEVE_RINGS = 14;
    static final int SLEEVE_ROUND = 32;

    /** The upper part of a robe on {@code proportions}, {@code ease} out from the body's contour, coloured {@code paint}. */
    public static Garment robe(final Proportions proportions, final double ease, final Paint paint) {
        return robe(new Envelope(proportions), ease, paint);
    }

    /** The upper part of a robe on the body {@code env} outlines, {@code ease} out from its contour, coloured {@code paint}. */
    public static Garment robe(final Envelope env, final double ease, final Paint paint) {
        final Rig rest = env.rest();
        final List<Piece> pieces = new ArrayList<>();
        pieces.add(body(env, rest, ease, paint));
        for (final int side : new int[] {1, -1}) {
            pieces.add(sleeve(env, rest, side, ease, paint));
        }
        return new Garment(rest, pieces);
    }

    /** How many rings round a pair of pants at the hips, and points round each; along each leg. */
    static final int HIP_RINGS = 14;
    static final int HIP_ROUND = 48;
    static final int LEG_RINGS = 10;

    /**
     * A pair of pants on the body {@code env} outlines, {@code ease} out from its contour, coloured {@code paint}: round
     * the hips and the tops of both thighs as one, from the crotch up to {@code rise} of the way to the top of the hips;
     * and, if {@code down} is more than nothing, down each thigh that share of the way to the knee. Its points moving
     * with the pelvis, and with the thigh they lie over, the lower the more. Its legs cut up toward the hips at the
     * sides by {@code cut}, cm, low at the crotch before and behind.
     */
    public static Garment pants(final Envelope env, final double ease, final double rise, final double down,
                                final double cut,
                                final Paint paint) {
        final Rig rest = env.rest();
        final List<Piece> pieces = new ArrayList<>();
        final V3 right = rest.frame(Part.RIGHT_THIGH).at();
        final V3 left = rest.frame(Part.LEFT_THIGH).at();
        final double crotch = rest.place(Part.PELVIS, new V3(0, -7.5, 0)).y();
        final double top = rest.place(Part.PELVIS, new V3(0, -9 + 24 * rise, 0)).y();
        final Set<Part> hips = EnumSet.of(Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_THIGH);
        final double[][] r = new double[HIP_RINGS][HIP_ROUND];
        final double[][] ys = new double[HIP_RINGS][HIP_ROUND];
        final double[] zs = new double[HIP_RINGS];
        for (int i = 0; i < HIP_RINGS; i++) {
            final double share = (double) i / (HIP_RINGS - 1);
            final double y = crotch + (top - crotch) * share;
            final V3 rough = new V3(0, y, 0);
            final double front = env.out(rough, V3.Z, hips, v -> true);
            final double back = env.out(rough, V3.Z.times(-1), hips, v -> true);
            zs[i] = front > 0 && back > 0 ? (front - back) / 2 : 0;
            for (int j = 0; j < HIP_ROUND; j++) {
                final double phi = 2 * Math.PI * j / HIP_ROUND;
                // the leg openings cut up at the sides, low at the crotch before and behind
                final double bottom = crotch + cut * Math.pow(Math.abs(Math.cos(phi)), 1.5);
                ys[i][j] = bottom + (top - bottom) * share;
                r[i][j] = env.out(new V3(0, ys[i][j], zs[i]), new V3(Math.cos(phi), 0, Math.sin(phi)), hips,
                        v -> true);
            }
        }
        fill(r);
        bridge(r, 4, 1);
        final List<List<Bound>> rings = new ArrayList<>();
        for (int i = 0; i < HIP_RINGS; i++) {
            final List<Bound> ring = new ArrayList<>();
            for (int j = 0; j < HIP_ROUND; j++) {
                final double phi = 2 * Math.PI * j / HIP_ROUND;
                final V3 p = new V3((r[i][j] + ease) * Math.cos(phi), ys[i][j],
                        zs[i] + (r[i][j] + ease) * Math.sin(phi));
                ring.add(hipBound(p, right, left));
            }
            rings.add(ring);
        }
        pieces.add(new Piece("pants", paint, rings, hipBound(new V3(0, crotch - 1, zs[0]), right, left),
                hipBound(new V3(0, top + 1, zs[HIP_RINGS - 1]), right, left)));
        if (down > 0) {
            for (final int side : new int[] {1, -1}) {
                pieces.add(leg(env, rest, side, ease, down, paint));
            }
        }
        return new Garment(rest, pieces);
    }

    /** What a point of a pair of pants at the hips moves with: the pelvis, and the thigh under it, the lower the more. */
    private static Bound hipBound(final V3 p, final V3 right, final V3 left) {
        final V3 hip = p.x() >= 0 ? right : left;
        final double toThigh = 0.6 * smooth(hip.y() + 4, hip.y() - 8, p.y());
        final Part thigh = p.x() >= 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
        return new Bound(p, new Part[] {Part.PELVIS, thigh}, new double[] {1 - toThigh, toThigh});
    }

    /** A pair of pants' leg on side {@code side}: down the thigh from below the crotch, {@code down} of the way to the knee. */
    private static Piece leg(final Envelope env, final Rig rest, final int side, final double ease, final double down,
                             final Paint paint) {
        final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
        final V3 hip = rest.frame(thigh).at();
        final V3 knee = rest.frame(side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN).at();
        final V3 u = knee.minus(hip).unit();
        final double length = knee.minus(hip).length();
        final V3 a = u.cross(V3.Z).unit();
        final V3 b = a.cross(u).unit();
        final double from = 0.1;
        final double[][] r = new double[LEG_RINGS][SLEEVE_ROUND];
        final V3[] middles = new V3[LEG_RINGS];
        for (int i = 0; i < LEG_RINGS; i++) {
            final double s = from + (down - from) * i / (LEG_RINGS - 1);
            middles[i] = hip.plus(u.times(length * s));
            for (int j = 0; j < SLEEVE_ROUND; j++) {
                final double phi = 2 * Math.PI * j / SLEEVE_ROUND;
                r[i][j] = env.out(middles[i], a.times(Math.cos(phi)).plus(b.times(Math.sin(phi))), EnumSet.of(thigh),
                        v -> true);
            }
        }
        fill(r);
        bridge(r, 2, 1);
        final List<List<Bound>> rings = new ArrayList<>();
        for (int i = 0; i < LEG_RINGS; i++) {
            final double toThigh = i == 0 ? 0.7 : 1;
            final List<Bound> ring = new ArrayList<>();
            for (int j = 0; j < SLEEVE_ROUND; j++) {
                final double phi = 2 * Math.PI * j / SLEEVE_ROUND;
                final V3 way = a.times(Math.cos(phi)).plus(b.times(Math.sin(phi)));
                ring.add(new Bound(middles[i].plus(way.times(r[i][j] + ease)), new Part[] {Part.PELVIS, thigh},
                        new double[] {1 - toThigh, toThigh}));
            }
            rings.add(ring);
        }
        return new Piece((side > 0 ? "right" : "left") + " leg", paint, rings,
                new Bound(middles[0].minus(u.times(2)), new Part[] {thigh}, new double[] {1}),
                new Bound(middles[LEG_RINGS - 1].plus(u.times(1)), new Part[] {thigh}, new double[] {1}));
    }

    /** The robe's body: from the waist up round the trunk and over the shoulders to round the neck. */
    private static Piece body(final Envelope env, final Rig rest, final double ease, final Paint paint) {
        final double waist = rest.place(Part.PELVIS, new V3(0, 6, 0)).y();
        final double neck = rest.frame(Part.NECK).at().y() + 1.5;
        final V3 rightShoulder = rest.frame(Part.RIGHT_UPPER_ARM).at();
        final V3 leftShoulder = rest.frame(Part.LEFT_UPPER_ARM).at();
        final double armpit = rightShoulder.y() - 3;
        final Set<Part> trunk = EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.NECK, Part.RIGHT_GIRDLE,
                Part.LEFT_GIRDLE);
        // the arms only over the tops of the shoulders, not hanging at the sides
        final Set<Part> arms = EnumSet.of(Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM);
        final java.util.function.Predicate<V3> high = v -> v.y() > armpit;
        final double[][] r = new double[BODY_RINGS][BODY_ROUND];
        final double[] ys = new double[BODY_RINGS];
        final double[] zs = new double[BODY_RINGS];
        for (int i = 0; i < BODY_RINGS; i++) {
            final double t = (double) i / (BODY_RINGS - 1);
            // closer together toward the shoulders and the neck, where the outline turns fastest
            final double s = 1 - Math.pow(1 - t, 1.6);
            final double y = waist + (neck - waist) * s;
            ys[i] = y;
            // its middle front to back, between the body's front and back there
            final V3 rough = new V3(0, y, rest.place(Part.CHEST, new V3(0, 6, 0)).z() * s);
            final double front = env.out(rough, V3.Z, trunk, v -> true);
            final double back = env.out(rough, V3.Z.times(-1), trunk, v -> true);
            zs[i] = front > 0 && back > 0 ? rough.z() + (front - back) / 2 : rough.z();
            final V3 middle = new V3(0, y, zs[i]);
            for (int j = 0; j < BODY_ROUND; j++) {
                final double phi = 2 * Math.PI * j / BODY_ROUND;
                final V3 way = new V3(Math.cos(phi), 0, Math.sin(phi));
                r[i][j] = Math.max(env.out(middle, way, trunk, v -> true), env.out(middle, way, arms, high));
            }
        }
        fill(r);
        bridge(r, 4, 1);
        final double yAbdomen = rest.frame(Part.ABDOMEN).at().y();
        final double yChest = rest.frame(Part.CHEST).at().y();
        final List<List<Bound>> rings = new ArrayList<>();
        for (int i = 0; i < BODY_RINGS; i++) {
            final List<Bound> ring = new ArrayList<>();
            for (int j = 0; j < BODY_ROUND; j++) {
                final double phi = 2 * Math.PI * j / BODY_ROUND;
                final V3 p = new V3((r[i][j] + ease) * Math.cos(phi), ys[i],
                        zs[i] + (r[i][j] + ease) * Math.sin(phi));
                ring.add(bodyBound(p, yAbdomen, yChest, rightShoulder, leftShoulder));
            }
            rings.add(ring);
        }
        final Bound start = bodyBound(new V3(0, waist - 2, zs[0]), yAbdomen, yChest, rightShoulder, leftShoulder);
        final Bound end = bodyBound(new V3(0, neck + 2, zs[BODY_RINGS - 1]), yAbdomen, yChest, rightShoulder,
                leftShoulder);
        return new Piece("robe", paint, rings, start, end);
    }

    /**
     * What a point of the robe's body moves with: the pelvis, the abdomen or the chest, as high as it is, blended over
     * the joints between; near the top of a shoulder, the arm too, as much as half.
     */
    private static Bound bodyBound(final V3 p, final double yAbdomen, final double yChest, final V3 right,
                                   final V3 left) {
        final double toAbdomen = smooth(yAbdomen - 4, yAbdomen + 4, p.y());
        final double toChest = smooth(yChest - 4, yChest + 4, p.y());
        double pelvis = 1 - toAbdomen;
        double abdomen = toAbdomen - toChest;
        double chest = toChest;
        final V3 shoulder = p.x() >= 0 ? right : left;
        final double near = p.y() > shoulder.y() - 10 ? 0.5 * (1 - smooth(5, 16, p.minus(shoulder).length())) : 0;
        pelvis *= 1 - near;
        abdomen *= 1 - near;
        chest *= 1 - near;
        final Part arm = p.x() >= 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
        return new Bound(p, new Part[] {Part.PELVIS, Part.ABDOMEN, Part.CHEST, arm},
                new double[] {pelvis, abdomen, chest, near});
    }

    /** A sleeve on side {@code side}: from the shoulder down the upper arm to the elbow. */
    private static Piece sleeve(final Envelope env, final Rig rest, final int side, final double ease,
                                final Paint paint) {
        final Part upper = side > 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
        final Part girdle = side > 0 ? Part.RIGHT_GIRDLE : Part.LEFT_GIRDLE;
        final V3 shoulder = rest.frame(upper).at();
        final V3 elbow = rest.frame(side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM).at();
        final V3 u = elbow.minus(shoulder).unit();
        final double length = elbow.minus(shoulder).length();
        final V3 a = u.cross(V3.Z).unit();
        final V3 b = a.cross(u).unit();
        final Set<Part> arm = EnumSet.of(upper, girdle);
        final double[][] r = new double[SLEEVE_RINGS][SLEEVE_ROUND];
        final V3[] middles = new V3[SLEEVE_RINGS];
        for (int i = 0; i < SLEEVE_RINGS; i++) {
            final double s = (double) i / (SLEEVE_RINGS - 1);
            middles[i] = shoulder.plus(u.times(length * s));
            for (int j = 0; j < SLEEVE_ROUND; j++) {
                final double phi = 2 * Math.PI * j / SLEEVE_ROUND;
                final V3 way = a.times(Math.cos(phi)).plus(b.times(Math.sin(phi)));
                r[i][j] = env.out(middles[i], way, arm, v -> true);
            }
        }
        fill(r);
        bridge(r, 3, 1);
        final List<List<Bound>> rings = new ArrayList<>();
        for (int i = 0; i < SLEEVE_RINGS; i++) {
            final double s = (double) i / (SLEEVE_RINGS - 1);
            final double toArm = 0.5 + 0.5 * smooth(0, 0.35, s);
            final List<Bound> ring = new ArrayList<>();
            for (int j = 0; j < SLEEVE_ROUND; j++) {
                final double phi = 2 * Math.PI * j / SLEEVE_ROUND;
                final V3 way = a.times(Math.cos(phi)).plus(b.times(Math.sin(phi)));
                ring.add(new Bound(middles[i].plus(way.times(r[i][j] + ease)), new Part[] {Part.CHEST, upper},
                        new double[] {1 - toArm, toArm}));
            }
            rings.add(ring);
        }
        final Bound start = new Bound(shoulder.minus(u.times(3)), new Part[] {Part.CHEST, upper},
                new double[] {0.5, 0.5});
        final Bound end = new Bound(elbow.plus(u.times(2)), new Part[] {upper}, new double[] {1});
        return new Piece((side > 0 ? "right" : "left") + " sleeve", paint, rings, start, end);
    }

    /** Where a line missed the body, the nearest that did not, round its ring, or the ring below's. */
    private static void fill(final double[][] r) {
        for (int i = 0; i < r.length; i++) {
            final int n = r[i].length;
            for (int j = 0; j < n; j++) {
                if (r[i][j] > 0) {
                    continue;
                }
                double found = -1;
                for (int k = 1; k < n / 2 && found < 0; k++) {
                    if (r[i][(j + k) % n] > 0) {
                        found = r[i][(j + k) % n];
                    } else if (r[i][Math.floorMod(j - k, n)] > 0) {
                        found = r[i][Math.floorMod(j - k, n)];
                    }
                }
                r[i][j] = found > 0 ? found : i > 0 ? r[i - 1][j] : 1;
            }
        }
    }

    /**
     * The hollows bridged, as cloth lies across them: each point out at least as far as halfway between its
     * neighbours, round its ring {@code round} times, and along {@code along} times.
     */
    private static void bridge(final double[][] r, final int round, final int along) {
        for (int pass = 0; pass < round; pass++) {
            for (final double[] ring : r) {
                final int n = ring.length;
                final double[] was = ring.clone();
                for (int j = 0; j < n; j++) {
                    ring[j] = Math.max(was[j], (was[(j + 1) % n] + was[Math.floorMod(j - 1, n)]) / 2);
                }
            }
        }
        for (int pass = 0; pass < along; pass++) {
            final double[][] was = new double[r.length][];
            for (int i = 0; i < r.length; i++) {
                was[i] = r[i].clone();
            }
            for (int i = 1; i + 1 < r.length; i++) {
                for (int j = 0; j < r[i].length; j++) {
                    r[i][j] = Math.max(was[i][j], (was[i - 1][j] + was[i + 1][j]) / 2);
                }
            }
        }
    }

    static double smooth(final double from, final double to, final double v) {
        final double t = Math.clamp((v - from) / (to - from), 0, 1);
        return t * t * (3 - 2 * t);
    }
}

package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.foot.Toe;
import com.example.fxsuite.life.body.bone.foot.Toes;
import com.example.fxsuite.life.body.cage.Building;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 * The fat cage's right foot, going on from the last of its leg's rings, along the foot ({@link FatCage}): the forefoot
 * in two rings across it, as many points over its top as under its sole, lanes in line with the toes, the great toe
 * twice as many as each of the others, the second at the balls of the foot, where each toe's metatarsal's head is;
 * the leg's ring joined to the first ({@link Zip}); each toe hung from its lanes there, a tube along its bones as they
 * lie ({@link Toes}), closed over its tip; between each toe and the next, its web. Each point on the outside along a
 * line in, among what is its own: the forefoot's, or one toe's. Every face made to go round the way the leg's do.
 */
final class FootCage {

    /** How many points over the top of the foot, and under it. */
    static final int ACROSS = 12;
    /** How far along the foot the forefoot's first ring is, cm, in the foot's frame. */
    private static final double FOREFOOT = 10.5;

    private final FatTarget target;
    private final Rig.Frame foot;
    private final Rig.Frame toes;

    FootCage(final FatTarget target) {
        this.target = target;
        this.foot = target.bind().frame(Part.RIGHT_FOOT);
        this.toes = target.bind().frame(Part.RIGHT_TOES);
    }

    /**
     * The forefoot and the toes hung from {@code ring}, set out along {@code ways}; every point put in {@code held},
     * wholly but the forefoot's first ring, a little.
     */
    void hang(final Building b, final int[] ring, final V3[] ways, final java.util.Map<Integer, Double> held) {
        final int from = b.size();
        final IntPredicate fore = forefoot();
        final int[][] rings = {across(b, fore, false), across(b, fore, true)};
        b.making(Part.RIGHT_FOOT);
        new Zip(b).join(ring, rings[0], foot.way(V3.Z), ways, forefootWays());
        final int faces = b.faceCount();
        final int n = 2 * ACROSS;
        for (int j = 0; j < n; j++) {
            b.face(rings[0][j], rings[0][(j + 1) % n], rings[1][(j + 1) % n], rings[1][j]);
        }
        final int[] balls = rings[1];
        b.making(Part.RIGHT_TOES);
        final int last = n - 1;
        toe(b, Toe.GREAT, new int[] {balls[0], balls[1], balls[2], balls[3], balls[last - 3], balls[last - 2],
            balls[last - 1], balls[last]}, 1.6);
        for (int i = 1; i < Toe.values().length; i++) {
            final int a = 2 + 2 * i;
            toe(b, Toe.values()[i], new int[] {balls[a], balls[a + 1], balls[last - a - 1], balls[last - a]}, 1.0);
        }
        for (int i = 0; i < 4; i++) {
            final int a = 3 + 2 * i;
            b.face(balls[a], balls[a + 1], balls[last - a - 1], balls[last - a]);
        }
        b.agree(faces);
        for (int i = from; i < b.size(); i++) {
            held.put(i, 1.0);
        }
        for (final int p : rings[0]) {
            held.put(p, 0.4);
        }
    }

    /** What is the forefoot's: the foot's and the toes' bones and muscles before the instep. */
    private IntPredicate forefoot() {
        final IntPredicate f = target.among(Part.RIGHT_FOOT, p -> foot.local(p).z() > 6);
        final IntPredicate t = target.among(Part.RIGHT_TOES, p -> true);
        return i -> f.test(i) || t.test(i);
    }

    /**
     * A ring across the forefoot: over its top from the great toe's side to the little toe's, then back under its
     * sole; at the balls of the foot if {@code balls}, each lane where its toe's metatarsal's head is, or before them.
     */
    private int[] across(final Building b, final IntPredicate fore, final boolean balls) {
        final int[] ring = new int[2 * ACROSS];
        for (int j = 0; j < ACROSS; j++) {
            final double x = lane(j, balls);
            final double z = balls ? toeOf(j).head().z() : FOREFOOT;
            ring[j] = b.add(onto(new V3(x, 4, z), -1, fore), "right foot, top " + (balls ? 1 : 0) + ", " + j);
            ring[2 * ACROSS - 1 - j] = b.add(onto(new V3(x, -12, z), 1, fore),
                    "right foot, sole " + (balls ? 1 : 0) + ", " + j);
        }
        return ring;
    }

    /** The toe lane {@code j} is in line with. */
    private Toe toeOf(final int j) {
        return j < 4 ? Toe.GREAT : Toe.values()[(j - 4) / 2 + 1];
    }

    /** How far across lane {@code j} is, in the foot's frame, at the balls of the foot or before them. */
    private double lane(final int j, final boolean balls) {
        final Toe t = toeOf(j);
        final double[] great = {-1.05, -0.4, 0.3, 0.75};
        final double off = j < 4 ? great[j] : (j % 2 == 0 ? -0.45 : 0.45) + (j == ACROSS - 1 ? 0.35 : 0);
        final double x = t.base().x() + (t.head().x() - t.base().x()) * (balls ? 1 : 0.6);
        return x + off * (balls ? 1 : 1.15);
    }

    /** Which way out each point of the forefoot's first ring was set along: from its middle, up or down. */
    private V3[] forefootWays() {
        final V3[] ways = new V3[2 * ACROSS];
        for (int j = 0; j < ACROSS; j++) {
            ways[j] = foot.way(new V3(lane(j, false), 1.6, 0)).unit();
            ways[2 * ACROSS - 1 - j] = foot.way(new V3(lane(j, false), -1.6, 0)).unit();
        }
        return ways;
    }

    /** The outside on the line from {@code local}, in the foot's frame, along its {@code y} as {@code sign} says. */
    private V3 onto(final V3 local, final int sign, final IntPredicate counts) {
        return target.onto(foot.place(local), foot.way(V3.Y.times(sign)), counts, 16).map(FatTarget.Hit::at)
                .orElse(foot.place(new V3(local.x(), sign < 0 ? -2.5 : -6.2, local.z())));
    }

    /** Toe {@code t} hung from {@code loop}, its lines starting {@code out} cm out, closed over its tip. */
    private void toe(final Building b, final Toe t, final int[] loop, final double out) {
        final IntPredicate own = target.among(Part.RIGHT_TOES, p -> nearest(p) == t);
        final List<Limb.Ring> rings = new ArrayList<>();
        for (int q = 0; q < t.phalanges(); q++) {
            final V3[] e = Toes.INSTANCE.ends(t, q);
            final V3 a = toes.place(e[0]);
            final V3 z = toes.place(e[1]);
            final V3 way = z.minus(a).unit();
            final V3 up = foot.way(V3.Y);
            final V3 w = up.minus(way.times(up.dot(way))).unit();
            for (final double f : q == 0 && t == Toe.GREAT ? new double[] {0.35, 0.8} : new double[] {0.5}) {
                rings.add(new Limb.Ring(a.plus(z.minus(a).times(f)), way.cross(w).unit(), w,
                        Double.POSITIVE_INFINITY, out, "right " + t.said(), Part.RIGHT_TOES));
            }
        }
        final int[] ring = new Limb(target).hang(b, loop, own, rings, false, Double.NaN);
        final V3[] e = Toes.INSTANCE.ends(t, t.phalanges() - 1);
        final V3 end = toes.place(e[1]);
        final V3 way = end.minus(toes.place(e[0])).unit();
        final int at = b.add(target.onto(end.plus(way.times(out)), way.times(-1), own, 2 * out)
                .map(FatTarget.Hit::at).orElse(end.plus(way.times(0.3))), "right " + t.said() + ", tip");
        for (int k = 0; k < ring.length; k++) {
            b.face(ring[k], ring[(k + 1) % ring.length], at);
        }
    }

    /** The toe whose bones {@code p}, in the body, is nearest. */
    private Toe nearest(final V3 p) {
        Toe best = Toe.GREAT;
        double near = Double.MAX_VALUE;
        for (final Toe t : Toe.values()) {
            for (int q = 0; q < t.phalanges(); q++) {
                final V3[] e = Toes.INSTANCE.ends(t, q);
                final double far = apart(p, toes.place(e[0]), toes.place(e[1]));
                if (far < near) {
                    near = far;
                    best = t;
                }
            }
        }
        return best;
    }

    private double apart(final V3 p, final V3 a, final V3 b) {
        final V3 ab = b.minus(a);
        final double s = Math.clamp(p.minus(a).dot(ab) / ab.dot(ab), 0, 1);
        return p.minus(a.plus(ab.times(s))).length();
    }
}

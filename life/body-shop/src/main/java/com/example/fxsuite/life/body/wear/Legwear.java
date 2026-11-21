package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Knit drawn tight from above the waist down both legs over the feet to the toes, as everything worn on the legs is
 * cut from ({@link Hoop}): rings round the hips, level; from under the crotch, where a seam runs from the front to the
 * back between the legs, rings round each leg, level, down to the ankle; there turning, each ring through a line
 * across the front of the ankle, from level to upright, round the heel; then upright rings along the foot to the
 * toes, closed over them. Each leg's rings read round the same way, every point of one ring going on to the next as a
 * column of the knit, the leg's first ring the half of the hips' last on its side and the seam. The right side made,
 * the left its mirror ({@link Sheet#mirrored}).
 */
public record Legwear() implements StatelessFunctionalObject {

    public static final Legwear INSTANCE = new Legwear();

    /** How many points round half the hips, along the seam, and how far apart the rings are, cm. */
    static final int HALF = 32;
    static final int SEAM = 8;
    static final double APART = 1.4;

    /** Where it is made to, as the body is held to be bound: the crotch, the top, the turn at the ankle, the toes. */
    public record Marks(double crotch, double top, V3 hinge, double toes) {
    }

    /** Knit {@code ease} out from {@code beneath}, where {@code bind} holds the body, to the marks {@code m}. */
    public Sheet sheet(final Beneath beneath, final Rig bind, final Marks m, final double ease) {
        final Sheet s = new Sheet();
        final int[][] hips = hips(s, beneath, m, ease);
        final int[] first = new int[HALF + SEAM];
        System.arraycopy(hips[0], 0, first, 0, HALF + 1);
        for (int j = 1; j < SEAM; j++) {
            // the seam from the front to the back, between the legs
            final double t = (double) j / SEAM;
            first[HALF + j] = s.add(s.point(hips[0][HALF]).plus(s.point(hips[0][0]).minus(s.point(hips[0][HALF]))
                    .times(t)), weight(s, hips[0][HALF], hips[0][0], t));
        }
        leg(s, beneath, bind, m, ease, first);
        return s.mirrored();
    }

    private Weights weight(final Sheet s, final int a, final int b, final double t) {
        return s.weightsAt(a).toward(s.weightsAt(b), t);
    }

    /** The rings round the hips, from the crotch up, each half round, back to front by the right. */
    private int[][] hips(final Sheet s, final Beneath b, final Marks m, final double ease) {
        final int[] among = among(b, p -> p.y() > m.crotch() - 6 && p.y() < m.top() + 6);
        final double[] angles = new double[HALF + 1];
        for (int k = 0; k <= HALF; k++) {
            angles[k] = -Math.PI / 2 + Math.PI * k / HALF;
        }
        final int rows = Math.max(2, (int) Math.round((m.top() - m.crotch()) / APART) + 1);
        final int[][] at = new int[rows][];
        for (int r = 0; r < rows; r++) {
            final double y = m.crotch() + (m.top() - m.crotch()) * r / (rows - 1);
            final Hoop.Ring ring = Hoop.INSTANCE.ring(b, among, new Hoop.Plane(new V3(0, y, 0), V3.X, V3.Z),
                    p -> true, angles, ease);
            at[r] = new int[HALF + 1];
            for (int k = 0; k <= HALF; k++) {
                final V3 p = ring.points().get(k);
                at[r][k] = s.add(k == 0 || k == HALF ? new V3(0, p.y(), p.z()) : p, ring.weights().get(k));
            }
            if (r > 0) {
                for (int k = 0; k < HALF; k++) {
                    s.quad(at[r - 1][k], at[r][k], at[r][k + 1], at[r - 1][k + 1]);
                }
            }
        }
        return at;
    }

    /** The right leg's rings, from {@code first} under the crotch down to the ankle, round the heel, to the toes. */
    private void leg(final Sheet s, final Beneath b, final Rig bind, final Marks m, final double ease,
                     final int[] first) {
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        final V3 knee = bind.frame(Part.RIGHT_SHIN).at();
        final V3 axis = hip.plus(knee.minus(hip).times((hip.y() - m.crotch()) / (hip.y() - knee.y())));
        final double[] from = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            final V3 p = s.point(first[i]);
            from[i] = Math.atan2(p.z() - axis.z(), p.x() - axis.x());
            while (i > 0 && from[i] < from[i - 1]) {
                from[i] += 2 * Math.PI;
            }
        }
        final List<Ring> rings = new ArrayList<>();
        final int[] legs = among(b, p -> p.x() > -1 && p.y() < m.crotch() + 6);
        final int down = (int) Math.round((m.crotch() - m.hinge().y()) / APART);
        for (int j = 1; j <= down; j++) {
            final double y = m.crotch() - (m.crotch() - m.hinge().y()) * j / down;
            final double blend = Math.min(1, (m.crotch() - y) / 15);
            rings.add(new Ring(new Hoop.Plane(new V3(0, y, 0), V3.X, V3.Z), p -> p.x() > 0, angles(from, blend),
                    legs));
        }
        Foot.INSTANCE.rings(b, m, from, rings);
        int[] last = first;
        for (final Ring r : rings) {
            final Hoop.Ring ring = Hoop.INSTANCE.ring(b, r.among(), r.plane(), r.keep(), r.angles(), ease);
            if (ring == null) {
                continue;
            }
            final int[] next = new int[first.length];
            for (int i = 0; i < next.length; i++) {
                next[i] = s.add(ring.points().get(i), ring.weights().get(i));
            }
            for (int i = 0; i < next.length; i++) {
                final int k = (i + 1) % next.length;
                s.quad(last[i], last[k], next[k], next[i]);
            }
            last = next;
        }
        close(s, last);
    }

    /** One ring to be found: its plane, what of the body counts in it, its points' ways round, among which. */
    record Ring(Hoop.Plane plane, Predicate<V3> keep, double[] angles, int[] among) {
    }

    /** The ways round of the leg's first ring, {@code from}, gone as far as {@code blend} toward even ways round. */
    double[] angles(final double[] from, final double blend) {
        final double k = blend * blend * (3 - 2 * blend);
        final double[] a = new double[from.length];
        for (int i = 0; i < a.length; i++) {
            a[i] = from[i] + (from[0] + 2 * Math.PI * i / from.length - from[i]) * k;
        }
        return a;
    }

    /** The toes closed over: a point before the last ring's middle, every point of it joined to it. */
    private void close(final Sheet s, final int[] last) {
        V3 middle = V3.ZERO;
        for (final int i : last) {
            middle = middle.plus(s.point(i));
        }
        middle = middle.times(1.0 / last.length);
        final int tip = s.add(middle.plus(V3.Z.times(0.8)), s.weightsAt(last[0]));
        for (int i = 0; i < last.length; i++) {
            s.triangle(last[i], last[(i + 1) % last.length], tip);
        }
    }

    /**
     * The triangles of {@code b} whose first point lies where {@code where} says, but for the arms': whatever part
     * a point goes with most, a muscle across a joint the knit covers, it counts where it lies.
     */
    int[] among(final Beneath b, final Predicate<V3> where) {
        final Set<Part> arms = EnumSet.of(Part.RIGHT_GIRDLE, Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM,
                Part.RIGHT_HAND, Part.LEFT_GIRDLE, Part.LEFT_UPPER_ARM, Part.LEFT_FOREARM, Part.LEFT_HAND);
        final int[] t = b.triangles();
        final List<Integer> out = new ArrayList<>();
        for (int i = 0; i < t.length / 3; i++) {
            if (!arms.contains(b.main()[t[3 * i]]) && where.test(b.point(t[3 * i]))) {
                out.add(i);
            }
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }
}

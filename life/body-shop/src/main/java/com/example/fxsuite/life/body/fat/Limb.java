package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;

import java.util.List;
import java.util.function.IntPredicate;

/**
 * A limb of the fat cage, hung from a loop of the torso's points where it goes out ({@link FatCage}): rings, each in a
 * plane of its own ({@link Ring}), as many points round as its loop, evenly round, the first at the angle round of the
 * loop's first and on the way the loop goes; each point on the fat's outside along a line in to the ring's middle,
 * among what of the limb counts ({@link FatTarget}); where a line meets nothing, as far out as the ring before.
 * Four-sided faces between one ring and the next, the first ring's and the loop's, of the next ring's part; its end
 * shut by one face, or left open for what goes on from it, a hand. On the right.
 */
final class Limb {

    /**
     * How far a ring's point is looked for along its line in, as many times as far as it starts out: on past the
     * ring's middle, which may lie at the limb's edge and not within it, the forearm's by the wrist, all of whose
     * flesh lies before the line through its joints; so that a point on the side where none is meets the limb beyond
     * the middle, and is not left where it began, standing off the limb as a fin.
     */
    private static final double PAST = 2;

    private final FatTarget target;
    private V3[] ways = new V3[0];

    /**
     * A ring's plane: its middle; its two ways, {@code u} to the body's right and {@code w} to its front, or what the
     * front turns into round a heel; no further out toward {@code w} than {@code toward}, the line it turns through;
     * how far out its lines start, {@code out}; the part the faces up to it are of, {@code part}.
     */
    record Ring(V3 middle, V3 u, V3 w, double toward, double out, String said, Part part) {

        /** The same ring, the faces up to it of part {@code p}. */
        Ring of(final Part p) {
            return new Ring(middle, u, w, toward, out, said, p);
        }
    }

    Limb(final FatTarget target) {
        this.target = target;
    }

    /**
     * The limb among what {@code counts} says through {@code rings}, hung from {@code loop}, added to {@code b}; its
     * end shut if {@code shut}. Its last ring. Each ring's first
     * point as far round as {@code phase}, if it is a number, every body's the same way; or as far round as the loop's
     * first point is.
     */
    int[] hang(final Building b, final int[] loop, final IntPredicate counts, final List<Ring> rings,
               final boolean shut, final double phase) {
        final int n = loop.length;
        final Ring first = rings.getFirst();
        final double[] round = new double[n];
        final double turned = round(b, loop, first, round);
        if (!Double.isNaN(phase)) {
            round[0] = phase;
        }
        int[] above = loop;
        double reach = first.out();
        for (final Ring ring : rings) {
            final int[] at = new int[n];
            double far = 0;
            ways = new V3[n];
            for (int k = 0; k < n; k++) {
                final double a = round[0] + Math.signum(turned) * 2 * Math.PI * k / n;
                final V3 out = ring.u().times(Math.sin(a)).plus(ring.w().times(Math.cos(a))).unit();
                ways[k] = out;
                final double start = Math.cos(a) > 0 ? Math.min(ring.out(), 0.95 * ring.toward() / Math.cos(a))
                        : ring.out();
                final double before = reach;
                final V3 p = target.onto(ring.middle().plus(out.times(start)), out.times(-1), counts, PAST * start)
                        .map(FatTarget.Hit::at)
                        .orElse(ring.middle().plus(out.times(Math.min(before, start))));
                far = Math.max(far, p.minus(ring.middle()).length());
                at[k] = b.add(p, ring.said() + ", " + k);
            }
            reach = Math.min(ring.out(), far);
            b.making(ring.part());
            for (int k = 0; k < n; k++) {
                final int next = (k + 1) % n;
                b.face(at[k], at[next], above[next], above[k]);
            }
            above = at;
        }
        if (shut) {
            shut(b, above);
        }
        return above;
    }

    /** Which way out each point of the last ring hung was set along: every body's the same, if its phase was given. */
    V3[] ways() {
        return ways.clone();
    }

    /**
     * How far round each point of {@code loop} is about its middle, read in {@code first}'s plane, into {@code round};
     * how far round they go, all told, the way they go.
     */
    private double round(final Building b, final int[] loop, final Ring first, final double[] round) {
        final int n = loop.length;
        V3 mid = V3.ZERO;
        for (final int p : loop) {
            mid = mid.plus(b.point(p).times(1.0 / n));
        }
        V3 across = V3.ZERO;
        for (int k = 0; k < n; k++) {
            final V3 v = b.point(loop[k]).minus(mid);
            round[k] = Math.atan2(v.dot(first.u()), v.dot(first.w()));
            across = across.plus(v.cross(b.point(loop[(k + 1) % n]).minus(mid)));
        }
        // the way they go round told by the area they span, not by how far round they wind seen in the ring's plane,
        // which a loop seen nearly edge on, folded over, may make nothing
        return 2 * Math.PI * Math.signum(across.dot(first.w().cross(first.u())));
    }


    /** The end of the limb, past {@code above}, shut by one face. */
    private void shut(final Building b, final int[] above) {
        final int n = above.length;
        final int[] shut = new int[n];
        for (int k = 0; k < n; k++) {
            shut[k] = above[n - 1 - k];
        }
        b.face(shut);
    }
}

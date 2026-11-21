package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;
import com.example.fxsuite.life.body.wear.Hoop;

import java.util.List;

/**
 * A limb of the fat cage, hung from a loop of the torso's points where it goes out ({@link FatCage}): rings, each in a
 * plane of its own ({@link Ring}), as many points round as its loop, evenly round, the first at the angle round of the
 * loop's first and on the way the loop goes; each point on the fat's outside along a line in to the ring's middle,
 * among what of the limb counts ({@link FatTarget}); where a line meets nothing, as far out as the ring before.
 * Four-sided faces between one ring and the next, the first ring's and the loop's; its end shut by one face. On the
 * right.
 */
final class Limb {

    /** How far out the lines start, cm. */
    private static final double OUT = 15;

    private final FatTarget target;

    /**
     * A ring's plane: its middle; its two ways, {@code u} to the body's right and {@code w} to its front, or what the
     * front turns into round a heel; no further out toward {@code w} than {@code toward}, the line it turns through;
     * {@code wrapped} round what it cuts, as a mitten round the fingers, or on it.
     */
    record Ring(V3 middle, V3 u, V3 w, double toward, boolean wrapped, String said) {
    }

    Limb(final FatTarget target) {
        this.target = target;
    }

    /**
     * The limb on region {@code r} through {@code rings}, hung from {@code loop}, added to {@code b}; the points of its
     * wrapped rings put in {@code wrapped}.
     */
    void hang(final Building b, final int[] loop, final FatTarget.Region r, final List<Ring> rings,
              final java.util.Set<Integer> wrapped) {
        final int n = loop.length;
        V3 mid = V3.ZERO;
        for (final int p : loop) {
            mid = mid.plus(b.point(p).times(1.0 / n));
        }
        final Ring first = rings.getFirst();
        final double[] round = new double[n];
        double turned = 0;
        for (int k = 0; k < n; k++) {
            final V3 v = b.point(loop[k]).minus(mid);
            round[k] = Math.atan2(v.dot(first.u()), v.dot(first.w()));
        }
        for (int k = 0; k < n; k++) {
            turned += Math.IEEEremainder(round[(k + 1) % n] - round[k], 2 * Math.PI);
        }
        int[] above = loop;
        double reach = OUT;
        for (final Ring ring : rings) {
            final int[] at = new int[n];
            final V3[] found = ring.wrapped() ? wrap(ring, r, round[0], turned, n) : null;
            double far = 0;
            for (int k = 0; k < n; k++) {
                final double a = round[0] + Math.signum(turned) * 2 * Math.PI * k / n;
                final V3 out = ring.u().times(Math.sin(a)).plus(ring.w().times(Math.cos(a))).unit();
                final double start = Math.cos(a) > 0 ? Math.min(OUT, 0.95 * ring.toward() / Math.cos(a)) : OUT;
                final double before = reach;
                final V3 p = found != null ? found[k] : target.onto(ring.middle().plus(out.times(start)),
                        out.times(-1), r, 1, start).map(FatTarget.Hit::at)
                        .orElse(ring.middle().plus(out.times(Math.min(before, start))));
                far = Math.max(far, p.minus(ring.middle()).length());
                at[k] = b.add(p, ring.said() + ", " + k);
                if (ring.wrapped()) {
                    wrapped.add(at[k]);
                }
            }
            reach = Math.min(OUT, far);
            for (int k = 0; k < n; k++) {
                final int next = (k + 1) % n;
                b.face(at[k], at[next], above[next], above[k]);
            }
            above = at;
        }
        shut(b, above);
    }

    /**
     * {@code ring}'s points wrapped round what it cuts, {@code n} of them evenly round from {@code first}, the way
     * {@code turned} goes; none if it cuts nothing.
     */
    private V3[] wrap(final Ring ring, final FatTarget.Region r, final double first, final double turned,
                      final int n) {
        final double[] angles = new double[n];
        for (int k = 0; k < n; k++) {
            // a mitten's ways round are read from u toward w, the cage's from w toward u
            angles[k] = Math.PI / 2 - (first + Math.signum(turned) * 2 * Math.PI * k / n);
        }
        final Hoop.Ring h = target.wrap(new Hoop.Plane(ring.middle(), ring.u(), ring.w()), r, 1, angles);
        return h == null || h.points().size() < n ? null : h.points().toArray(new V3[0]);
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

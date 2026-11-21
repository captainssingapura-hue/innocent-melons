package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;

/**
 * A limb of the fat cage, hung from a loop of the torso's points where it goes out ({@link FatCage}): rings square to
 * its bones, at shares of the way along each from its joint to the next, as many points round as its loop, evenly
 * round, the first at the angle round of the loop's first and on the way the loop goes; each point on the fat's
 * outside along a line in to the bone, among what of the limb counts ({@link FatTarget}); where a line meets nothing,
 * as far out as the ring before. Four-sided faces between one ring and the next, the first ring's and the loop's; its
 * end shut by one face. On the right.
 */
final class Limb {

    /** How far out the lines start, cm. */
    private static final double OUT = 15;

    private final FatTarget target;

    Limb(final FatTarget target) {
        this.target = target;
    }

    /**
     * The limb on region {@code r} through {@code parts}' joints (each part's, and the next's, the last's its end),
     * hung from {@code loop}, its rings at {@code along} shares of each part's way, added to {@code b}.
     */
    void hang(final Building b, final int[] loop, final FatTarget.Region r, final Part[] parts,
              final double[][] along) {
        final int n = loop.length;
        final V3 first = target.bind().frame(parts[0]).at();
        final V3 way0 = target.bind().frame(parts[1]).at().minus(first).unit();
        V3 mid = V3.ZERO;
        for (final int p : loop) {
            mid = mid.plus(b.point(p).times(1.0 / n));
        }
        final double[] round = new double[n];
        double turned = 0;
        for (int k = 0; k < n; k++) {
            round[k] = angle(b.point(loop[k]).minus(mid), way0);
        }
        for (int k = 0; k < n; k++) {
            turned += Math.IEEEremainder(round[(k + 1) % n] - round[k], 2 * Math.PI);
        }
        int[] above = loop;
        double reach = OUT;
        for (int s = 0; s < along.length; s++) {
            final V3 from = target.bind().frame(parts[s]).at();
            final V3 to = target.bind().frame(parts[s + 1]).at();
            final V3 way = to.minus(from).unit();
            for (final double f : along[s]) {
                final V3 c = from.plus(to.minus(from).times(f));
                final int[] ring = new int[n];
                double far = 0;
                for (int k = 0; k < n; k++) {
                    final V3 out = across(way, round[0] + Math.signum(turned) * 2 * Math.PI * k / n);
                    final double before = reach;
                    final V3 p = target.onto(c.plus(out.times(OUT)), out.times(-1), r, 1, OUT)
                            .map(FatTarget.Hit::at).orElse(c.plus(out.times(before)));
                    far = Math.max(far, p.minus(c).length());
                    ring[k] = b.add(p, parts[s].said() + " " + Math.round(f * 100) + "%, " + k);
                }
                reach = Math.min(OUT, far);
                for (int k = 0; k < n; k++) {
                    final int next = (k + 1) % n;
                    b.face(ring[k], ring[next], above[next], above[k]);
                }
                above = ring;
            }
        }
        final int[] shut = new int[n];
        for (int k = 0; k < n; k++) {
            shut[k] = above[n - 1 - k];
        }
        b.face(shut);
    }

    /** The angle round {@code way} of {@code v}, from the body's front toward its right. */
    private double angle(final V3 v, final V3 way) {
        final V3[] uw = basis(way);
        return Math.atan2(v.dot(uw[0]), v.dot(uw[1]));
    }

    /** The way out square to {@code way} at angle {@code a} round it, from the body's front toward its right. */
    private V3 across(final V3 way, final double a) {
        final V3[] uw = basis(way);
        return uw[0].times(Math.sin(a)).plus(uw[1].times(Math.cos(a))).unit();
    }

    /** Two ways square to {@code way} and to each other: toward the body's right, and toward its front. */
    private V3[] basis(final V3 way) {
        final V3 front = V3.Z.minus(way.times(way.z())).unit();
        return new V3[] {front.cross(way).unit(), front};
    }
}

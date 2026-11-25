package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The middle line a bent part follows, as a hydroformed tube's: smooth through each of its points in turn, from the
 * first to the last, without a corner. Walked in so many steps between one point and the next: where each step is,
 * which way the line runs there, and how far it is between its points, as a share, so whatever changes along it can
 * change in step.
 *
 * @param through the points it passes through, at least two
 * @param steps   how many steps it is walked in from one point to the next
 */
public record Course(List<Vec3> through, int steps) {

    public Course {
        through = List.copyOf(through);
        if (through.size() < 2 || steps < 1) {
            throw new IllegalArgumentException("a course runs through two points at least, in a step at least");
        }
    }

    /** Where it is at each step, from its first point to its last. */
    public List<Vec3> points() {
        final List<Vec3> points = new ArrayList<>();
        for (int k = 0; k < through.size() - 1; k++) {
            for (int s = 0; s < steps; s++) {
                points.add(at(k, (double) s / steps));
            }
        }
        points.add(through.getLast());
        return points;
    }

    /** Which way it runs at each step, as {@link #points} has them. */
    public List<Vec3> ways() {
        final List<Vec3> ways = new ArrayList<>();
        for (int k = 0; k < through.size() - 1; k++) {
            for (int s = 0; s < steps; s++) {
                ways.add(way(k, (double) s / steps));
            }
        }
        ways.add(way(through.size() - 2, 1));
        return ways;
    }

    /** At each step, which of its points it is past and how far on toward the next, as a share: {@code k + share}. */
    public List<Double> shares() {
        final List<Double> shares = new ArrayList<>();
        for (int k = 0; k < through.size() - 1; k++) {
            for (int s = 0; s < steps; s++) {
                shares.add(k + (double) s / steps);
            }
        }
        shares.add(through.size() - 1.0);
        return shares;
    }

    /** The step on it nearest {@code to}. */
    public Vec3 nearest(final Vec3 to) {
        Vec3 best = through.getFirst();
        for (final Vec3 p : points()) {
            if (p.minus(to).length() < best.minus(to).length()) {
                best = p;
            }
        }
        return best;
    }

    /** Where it is {@code t} of the way from point {@code k} to the next. */
    private Vec3 at(final int k, final double t) {
        final Vec3 p0 = point(k - 1);
        final Vec3 p1 = point(k);
        final Vec3 p2 = point(k + 1);
        final Vec3 p3 = point(k + 2);
        final double t2 = t * t;
        final double t3 = t2 * t;
        return p1.times(2).plus(p2.minus(p0).times(t)).plus(p0.times(2).minus(p1.times(5)).plus(p2.times(4))
                .minus(p3).times(t2)).plus(p1.times(3).minus(p0).minus(p2.times(3)).plus(p3).times(t3)).times(0.5);
    }

    /** Which way it runs {@code t} of the way from point {@code k} to the next. */
    private Vec3 way(final int k, final double t) {
        final Vec3 p0 = point(k - 1);
        final Vec3 p1 = point(k);
        final Vec3 p2 = point(k + 1);
        final Vec3 p3 = point(k + 2);
        final Vec3 d = p2.minus(p0).plus(p0.times(2).minus(p1.times(5)).plus(p2.times(4)).minus(p3).times(2 * t))
                .plus(p1.times(3).minus(p0).minus(p2.times(3)).plus(p3).times(3 * t * t)).times(0.5);
        return d.length() < 1e-12 ? p2.minus(p1).unit() : d.unit();
    }

    /** Its point {@code k}; before the first and past the last, the line carried straight on. */
    private Vec3 point(final int k) {
        if (k < 0) {
            return through.get(0).times(2).minus(through.get(1));
        }
        if (k >= through.size()) {
            final int last = through.size() - 1;
            return through.get(last).times(2).minus(through.get(last - 1));
        }
        return through.get(k);
    }
}

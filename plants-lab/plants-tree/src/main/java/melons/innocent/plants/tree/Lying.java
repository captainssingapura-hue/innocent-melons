package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * Which ways a cloud of points lies: the three ways square to one another along which it is spread most, next and
 * least, found as the cloud's spread about its middle is turned until it has no part that runs across two ways at
 * once (Jacobi's way). So a spray of twigs reaching out and up is enclosed along its reach, not along the world's
 * axes, which would enclose it more loosely.
 */
public record Lying(List<Vec3> points) {

    /** How many sweeps of turning are made, at most: three ways settle long before. */
    private static final int SWEEPS = 32;
    private static final double SETTLED = 1e-12;

    public Lying {
        points = List.copyOf(points);
        if (points.isEmpty()) {
            throw new IllegalArgumentException("a cloud of no points lies no way");
        }
    }

    /** Its middle: where its points are on the whole. */
    public Vec3 middle() {
        Vec3 sum = new Vec3(0, 0, 0);
        for (final Vec3 point : points) {
            sum = sum.plus(point);
        }
        return sum.times(1.0 / points.size());
    }

    /** The three ways it lies, each one long and square to the others, the way it is spread most first. */
    public List<Vec3> ways() {
        final double[][] spread = spread();
        final double[][] turned = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        for (int sweep = 0; sweep < SWEEPS && across(spread) > SETTLED; sweep++) {
            for (int p = 0; p < 2; p++) {
                for (int q = p + 1; q < 3; q++) {
                    turnedOut(spread, turned, p, q);
                }
            }
        }
        final Integer[] order = {0, 1, 2};
        java.util.Arrays.sort(order, (a, b) -> Double.compare(spread[b][b], spread[a][a]));
        final Vec3 first = column(turned, order[0]);
        final Vec3 second = column(turned, order[1]);
        return List.of(first, second, first.cross(second).unit());
    }

    /** How its points spread about its middle, each way against each. */
    private double[][] spread() {
        final Vec3 middle = middle();
        final double[][] spread = new double[3][3];
        for (final Vec3 point : points) {
            final double[] d = {point.x() - middle.x(), point.y() - middle.y(), point.z() - middle.z()};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    spread[i][j] += d[i] * d[j];
                }
            }
        }
        return spread;
    }

    /** How much of the spread still runs across two ways at once. */
    private double across(final double[][] spread) {
        final double all = Math.abs(spread[0][0]) + Math.abs(spread[1][1]) + Math.abs(spread[2][2]) + 1e-300;
        return (Math.abs(spread[0][1]) + Math.abs(spread[0][2]) + Math.abs(spread[1][2])) / all;
    }

    /** The spread turned about the third way so that ways {@code p} and {@code q} no longer run across each other. */
    private void turnedOut(final double[][] spread, final double[][] turned, final int p, final int q) {
        if (Math.abs(spread[p][q]) < 1e-300) {
            return;
        }
        final double theta = (spread[q][q] - spread[p][p]) / (2 * spread[p][q]);
        final double t = Math.signum(theta == 0 ? 1 : theta) / (Math.abs(theta) + Math.sqrt(theta * theta + 1));
        final double c = 1 / Math.sqrt(t * t + 1);
        final double s = t * c;
        for (int k = 0; k < 3; k++) {
            final double kp = spread[k][p];
            final double kq = spread[k][q];
            spread[k][p] = c * kp - s * kq;
            spread[k][q] = s * kp + c * kq;
        }
        for (int k = 0; k < 3; k++) {
            final double pk = spread[p][k];
            final double qk = spread[q][k];
            spread[p][k] = c * pk - s * qk;
            spread[q][k] = s * pk + c * qk;
        }
        for (int k = 0; k < 3; k++) {
            final double kp = turned[k][p];
            final double kq = turned[k][q];
            turned[k][p] = c * kp - s * kq;
            turned[k][q] = s * kp + c * kq;
        }
    }

    private Vec3 column(final double[][] turned, final int which) {
        return new Vec3(turned[0][which], turned[1][which], turned[2][which]).unit();
    }
}

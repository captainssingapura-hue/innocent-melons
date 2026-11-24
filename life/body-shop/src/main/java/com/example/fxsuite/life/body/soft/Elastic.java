package com.example.fxsuite.life.body.soft;

/**
 * Elements of a soft material, corotated, solved as constraints on their nodes (XPBD): for each tetrahedron, how far
 * it is sheared and stretched from its rest shape turned as it is now, {@code |F - R|}, {@code R} the turn nearest
 * {@code F}, held as softly as the material's shear modulus says; and how its room has changed, {@code det F - 1},
 * held as softly as its first Lamé number says. Both are nothing at rest, so an element at rest is not moved, and
 * held at their own room an element turned inside out is pushed back. The turn is found afresh each time, from the
 * last found, after Müller, Bender, Chentanez and Macklin's way of finding a deformation's turn. Its rest shape may
 * be asked smaller, by a scale, so that it draws itself in, and grown again. Each element is taken in turn and its
 * nodes moved at once, each by how free it is ({@code w}, none if fixed), so a sweep is one pass of Gauss and Seidel.
 */
final class Elastic {

    /** How many times at most an element's turn is bettered each time it is asked. */
    private static final int TURNS = 6;

    private final int[] tets;
    /** Each element's rest shape, inverted: nine numbers each, by rows. */
    private final double[] inverse;
    /** Each element's room at rest, cm3. */
    private final double[] room;
    private final double mu;
    private final double lambda;
    /** How much stiffer than the material a step holds it: the square of a step's time, for a solve at rest. */
    private final double quick;
    /** Each element's turn as last found, a quaternion each, its real part first. */
    private final double[] turn;
    private final double[] sheared;
    private final double[] squeezed;

    /**
     * The elements {@code tets} (four nodes each) of nodes at rest where {@code rest} has them, three numbers each,
     * of a material of shear modulus {@code mu} and first Lamé number {@code lambda}. Each element's corners taken so
     * that it goes round the way that gives it room more than none; each step holding it {@code quick} times as stiff.
     * Each element's rest shape shortened by {@code tension} of itself along {@code fibre} (three numbers each, one
     * long, or none), its room kept, so that, held at its ends, it pulls: as a muscle at rest is never slack.
     */
    Elastic(final int[] tets, final double[] rest, final double mu, final double lambda, final double quick,
            final double[] fibre, final double tension) {
        this.tets = tets.clone();
        this.mu = mu;
        this.lambda = lambda;
        this.quick = quick;
        final int count = tets.length / 4;
        inverse = new double[9 * count];
        room = new double[count];
        sheared = new double[count];
        squeezed = new double[count];
        turn = new double[4 * count];
        for (int t = 0; t < count; t++) {
            double[] d = edges(rest, t);
            double det = det(d);
            if (det < 0) {
                final int swap = this.tets[4 * t + 2];
                this.tets[4 * t + 2] = this.tets[4 * t + 3];
                this.tets[4 * t + 3] = swap;
                d = edges(rest, t);
                det = det(d);
            }
            d = tightened(d, fibre, t, tension);
            det = det(d);
            room[t] = det / 6;
            turn[4 * t] = 1;
            final double[] inv = new double[9];
            inv[0] = (d[4] * d[8] - d[5] * d[7]) / det;
            inv[1] = (d[2] * d[7] - d[1] * d[8]) / det;
            inv[2] = (d[1] * d[5] - d[2] * d[4]) / det;
            inv[3] = (d[5] * d[6] - d[3] * d[8]) / det;
            inv[4] = (d[0] * d[8] - d[2] * d[6]) / det;
            inv[5] = (d[2] * d[3] - d[0] * d[5]) / det;
            inv[6] = (d[3] * d[7] - d[4] * d[6]) / det;
            inv[7] = (d[1] * d[6] - d[0] * d[7]) / det;
            inv[8] = (d[0] * d[4] - d[1] * d[3]) / det;
            System.arraycopy(inv, 0, inverse, 9 * t, 9);
        }
    }

    /**
     * {@code d}, element {@code t}'s edges at rest, shortened by {@code tension} of itself along its fibre and widened
     * across it as much as keeps its room; as it is, if it has no fibre.
     */
    private double[] tightened(final double[] d, final double[] fibre, final int t, final double tension) {
        final double fx = fibre[3 * t];
        final double fy = fibre[3 * t + 1];
        final double fz = fibre[3 * t + 2];
        if (tension == 0 || fx * fx + fy * fy + fz * fz < 0.5) {
            return d;
        }
        final double along = 1 - tension;
        final double across = 1 / Math.sqrt(along);
        final double[] f = {fx, fy, fz};
        final double[] a = new double[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                a[3 * r + c] = (r == c ? across : 0) + (along - across) * f[r] * f[c];
            }
        }
        return times(a, d);
    }

    /** How many elements. */
    int count() {
        return room.length;
    }

    /** The constraints' forces summed so far let go: a new step begins. */
    void begun() {
        java.util.Arrays.fill(sheared, 0);
        java.util.Arrays.fill(squeezed, 0);
    }

    /** One sweep over every element, nodes at {@code x}, each as free as {@code w}, rest shapes {@code scale} big. */
    void swept(final double[] x, final double[] w, final double scale) {
        final double cube = scale * scale * scale;
        for (int t = 0; t < room.length; t++) {
            if (w[tets[4 * t]] == 0 && w[tets[4 * t + 1]] == 0 && w[tets[4 * t + 2]] == 0
                    && w[tets[4 * t + 3]] == 0) {
                continue;
            }
            final double alphaD = 1 / (mu * room[t] * cube * quick);
            sheared[t] += solved(x, w, t, scale, false, alphaD, sheared[t]);
            final double alphaH = 1 / (lambda * room[t] * cube * quick);
            squeezed[t] += solved(x, w, t, scale, true, alphaH, squeezed[t]);
        }
    }

    /**
     * Element {@code t}'s shear constraint, or its room's if {@code room}, solved once, softly as {@code alpha} says,
     * {@code lambda} of it so far; its nodes moved. How much more of it there is now.
     */
    private double solved(final double[] x, final double[] w, final int t, final double scale, final boolean volume,
                          final double alpha, final double so) {
        final double[] d = edges(x, t);
        final double[] b = new double[9];
        for (int i = 0; i < 9; i++) {
            b[i] = inverse[9 * t + i] / scale;
        }
        final double[] f = times(d, b);
        final double c;
        final double[] p = new double[9];
        if (volume) {
            c = det(f) - 1;
            cofactor(f, p);
        } else {
            final double[] r = turned(t, f);
            double sum = 0;
            for (int i = 0; i < 9; i++) {
                p[i] = f[i] - r[i];
                sum += p[i] * p[i];
            }
            c = Math.sqrt(sum);
            if (c < 1e-9) {
                return 0;
            }
            for (int i = 0; i < 9; i++) {
                p[i] /= c;
            }
        }
        // the gradient by each edge: p times b transposed, a column for each of the three far corners
        final double[] g = new double[12];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                double s = 0;
                for (int k = 0; k < 3; k++) {
                    s += p[3 * row + k] * b[3 * col + k];
                }
                g[3 * (col + 1) + row] = s;
                g[row] -= s;
            }
        }
        double weighed = alpha;
        for (int k = 0; k < 4; k++) {
            final double wk = w[tets[4 * t + k]];
            weighed += wk * (g[3 * k] * g[3 * k] + g[3 * k + 1] * g[3 * k + 1] + g[3 * k + 2] * g[3 * k + 2]);
        }
        if (weighed < 1e-12) {
            return 0;
        }
        final double more = (-c - alpha * so) / weighed;
        for (int k = 0; k < 4; k++) {
            final int n = tets[4 * t + k];
            for (int a = 0; a < 3; a++) {
                x[3 * n + a] += w[n] * g[3 * k + a] * more;
            }
        }
        return more;
    }

    /**
     * Element {@code t}'s turn nearest {@code f}, by rows: found from its last by turning it, again and again, about
     * the way that brings its columns nearest {@code f}'s, till it is still; kept for the next time.
     */
    private double[] turned(final int t, final double[] f) {
        double qw = turn[4 * t];
        double qx = turn[4 * t + 1];
        double qy = turn[4 * t + 2];
        double qz = turn[4 * t + 3];
        final double[] r = new double[9];
        for (int it = 0; it < TURNS; it++) {
            rotation(qw, qx, qy, qz, r);
            double ox = 0;
            double oy = 0;
            double oz = 0;
            double dot = 0;
            for (int i = 0; i < 3; i++) {
                ox += r[3 + i] * f[6 + i] - r[6 + i] * f[3 + i];
                oy += r[6 + i] * f[i] - r[i] * f[6 + i];
                oz += r[i] * f[3 + i] - r[3 + i] * f[i];
                dot += r[i] * f[i] + r[3 + i] * f[3 + i] + r[6 + i] * f[6 + i];
            }
            final double k = 1 / (Math.abs(dot) + 1e-9);
            ox *= k;
            oy *= k;
            oz *= k;
            final double angle = Math.sqrt(ox * ox + oy * oy + oz * oz);
            if (angle < 1e-9) {
                break;
            }
            final double s = Math.sin(angle / 2) / angle;
            final double c = Math.cos(angle / 2);
            final double bw = c * qw - s * (ox * qx + oy * qy + oz * qz);
            final double bx = c * qx + s * ox * qw + s * (oy * qz - oz * qy);
            final double by = c * qy + s * oy * qw + s * (oz * qx - ox * qz);
            final double bz = c * qz + s * oz * qw + s * (ox * qy - oy * qx);
            final double n = Math.sqrt(bw * bw + bx * bx + by * by + bz * bz);
            qw = bw / n;
            qx = bx / n;
            qy = by / n;
            qz = bz / n;
        }
        turn[4 * t] = qw;
        turn[4 * t + 1] = qx;
        turn[4 * t + 2] = qy;
        turn[4 * t + 3] = qz;
        rotation(qw, qx, qy, qz, r);
        return r;
    }

    /** Into {@code r}, by rows, the turn of the quaternion {@code w}, {@code x}, {@code y}, {@code z}. */
    private void rotation(final double w, final double x, final double y, final double z, final double[] r) {
        r[0] = 1 - 2 * (y * y + z * z);
        r[1] = 2 * (x * y - w * z);
        r[2] = 2 * (x * z + w * y);
        r[3] = 2 * (x * y + w * z);
        r[4] = 1 - 2 * (x * x + z * z);
        r[5] = 2 * (y * z - w * x);
        r[6] = 2 * (x * z - w * y);
        r[7] = 2 * (y * z + w * x);
        r[8] = 1 - 2 * (x * x + y * y);
    }

    /** How much room the elements of nodes at {@code x} take now, all together, cm3. */
    double room(final double[] x) {
        double sum = 0;
        for (int t = 0; t < room.length; t++) {
            sum += det(edges(x, t)) / 6;
        }
        return sum;
    }

    /** How many elements of nodes at {@code x} are turned inside out. */
    int inverted(final double[] x) {
        int n = 0;
        for (int t = 0; t < room.length; t++) {
            n += det(edges(x, t)) <= 0 ? 1 : 0;
        }
        return n;
    }

    /** Element {@code t}'s three edges from its first corner, as the columns of a matrix, by rows. */
    private double[] edges(final double[] x, final int t) {
        final int a = 3 * tets[4 * t];
        final double[] d = new double[9];
        for (int col = 0; col < 3; col++) {
            final int b = 3 * tets[4 * t + col + 1];
            for (int row = 0; row < 3; row++) {
                d[3 * row + col] = x[b + row] - x[a + row];
            }
        }
        return d;
    }

    private double[] times(final double[] a, final double[] b) {
        final double[] m = new double[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                m[3 * r + c] = a[3 * r] * b[c] + a[3 * r + 1] * b[3 + c] + a[3 * r + 2] * b[6 + c];
            }
        }
        return m;
    }

    private double det(final double[] m) {
        return m[0] * (m[4] * m[8] - m[5] * m[7]) - m[1] * (m[3] * m[8] - m[5] * m[6])
                + m[2] * (m[3] * m[7] - m[4] * m[6]);
    }

    /** Into {@code p}, how {@code f}'s determinant changes with each of its numbers: its cofactors, by rows. */
    private void cofactor(final double[] f, final double[] p) {
        p[0] = f[4] * f[8] - f[5] * f[7];
        p[1] = f[5] * f[6] - f[3] * f[8];
        p[2] = f[3] * f[7] - f[4] * f[6];
        p[3] = f[2] * f[7] - f[1] * f[8];
        p[4] = f[0] * f[8] - f[2] * f[6];
        p[5] = f[1] * f[6] - f[0] * f[7];
        p[6] = f[1] * f[5] - f[2] * f[4];
        p[7] = f[2] * f[3] - f[0] * f[5];
        p[8] = f[0] * f[4] - f[1] * f[3];
    }
}

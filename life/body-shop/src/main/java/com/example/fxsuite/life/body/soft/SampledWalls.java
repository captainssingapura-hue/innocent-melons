package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/**
 * Walls known on a grid over a region: how far outside them each of the grid's points is, cm, less than nothing inside;
 * read between points by weighing the eight about, and the way out of them found by how that changes. The grid reaches
 * a little beyond the region; a point beyond it reads as the grid's edge.
 */
final class SampledWalls implements Walls {

    private final double[] lo = new double[3];
    private final int[] n = new int[3];
    private final double cell;
    private final float[] far;

    /** A grid over {@code region}, {@code beyond} cm past it each way, its points {@code cell} apart, all far off. */
    SampledWalls(final SoftRegion region, final double cell, final double beyond) {
        this.cell = cell;
        final double[] l = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] h = {region.hi().x(), region.hi().y(), region.hi().z()};
        for (int c = 0; c < 3; c++) {
            lo[c] = l[c] - beyond;
            n[c] = (int) Math.ceil((h[c] - l[c] + 2 * beyond) / cell) + 1;
        }
        far = new float[n[0] * n[1] * n[2]];
    }

    /** How many points the grid has each way: across, up and ahead. */
    int[] counts() {
        return n.clone();
    }

    /** Where its first point is. */
    double[] lowest() {
        return lo.clone();
    }

    double cell() {
        return cell;
    }

    /** Its {@code i}th point, numbered across, then up, then ahead. */
    V3 point(final int i) {
        return new V3(lo[0] + i % n[0] * cell, lo[1] + i / n[0] % n[1] * cell, lo[2] + i / (n[0] * n[1]) * cell);
    }

    /** How far outside the walls its {@code i}th point is said to be. */
    void set(final int i, final double far) {
        this.far[i] = (float) far;
    }

    @Override
    public double distance(final double x, final double y, final double z) {
        final double u = Math.clamp((x - lo[0]) / cell, 0, n[0] - 1.0001);
        final double v = Math.clamp((y - lo[1]) / cell, 0, n[1] - 1.0001);
        final double w = Math.clamp((z - lo[2]) / cell, 0, n[2] - 1.0001);
        final int i = (int) u;
        final int j = (int) v;
        final int k = (int) w;
        final double a = u - i;
        final double b = v - j;
        final double c = w - k;
        double d = 0;
        for (int corner = 0; corner < 8; corner++) {
            final int di = corner & 1;
            final int dj = corner >> 1 & 1;
            final int dk = corner >> 2 & 1;
            d += far[((k + dk) * n[1] + j + dj) * n[0] + i + di] * (di == 1 ? a : 1 - a) * (dj == 1 ? b : 1 - b)
                    * (dk == 1 ? c : 1 - c);
        }
        return d;
    }

    @Override
    public V3 out(final double x, final double y, final double z) {
        final double e = cell / 2;
        final V3 g = new V3(distance(x + e, y, z) - distance(x - e, y, z),
                distance(x, y + e, z) - distance(x, y - e, z),
                distance(x, y, z + e) - distance(x, y, z - e));
        final double length = g.length();
        return length < 1e-12 ? V3.ZERO : g.times(1 / length);
    }
}

package melons.innocent.terrain.bench;

import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;

import java.util.stream.IntStream;

/**
 * The ground's heights over its extent, worked out once: at points {@code every} centimetres apart each way, the last
 * row and column at the extent's far edges, its rows worked out side by side, the ground being the same whoever asks
 * and whenever. What is drawn and what is coloured are both read from it, and the ground is asked nothing again. How
 * steep the ground is at a point is taken from the points either side of it.
 */
public final class Grid {

    private final double[] xs;
    private final double[] zs;
    private final double[][] heights;

    /** {@code ground}'s heights, about {@code every} centimetres apart. */
    public Grid(final Ground ground, final double every) {
        if (!(every > 0)) {
            throw new IllegalArgumentException("points some way apart: " + every);
        }
        final Extent extent = ground.extent();
        this.xs = spaced(extent.x0(), extent.x1(), every);
        this.zs = spaced(extent.z0(), extent.z1(), every);
        this.heights = new double[zs.length][xs.length];
        IntStream.range(0, zs.length).parallel().forEach(j -> {
            for (int i = 0; i < xs.length; i++) {
                heights[j][i] = ground.height(xs[i], zs[j]);
            }
        });
    }

    /** How many points there are across. */
    public int across() {
        return xs.length;
    }

    /** How many points there are along. */
    public int along() {
        return zs.length;
    }

    /** How far across point column {@code i} is. */
    public double x(final int i) {
        return xs[i];
    }

    /** How far along point row {@code j} is. */
    public double z(final int j) {
        return zs[j];
    }

    /** How high the ground is at point ({@code i}, {@code j}). */
    public double height(final int i, final int j) {
        return heights[j][i];
    }

    /** How far the ground rises at point ({@code i}, {@code j}), across and along, from the points either side. */
    public double[] rise(final int i, final int j) {
        final int left = Math.max(i - 1, 0);
        final int right = Math.min(i + 1, xs.length - 1);
        final int near = Math.max(j - 1, 0);
        final int far = Math.min(j + 1, zs.length - 1);
        return new double[] {(heights[j][right] - heights[j][left]) / (xs[right] - xs[left]),
            (heights[far][i] - heights[near][i]) / (zs[far] - zs[near])};
    }

    /** The ground at point ({@code i}, {@code j}), as worked out: where, how high and how steep. */
    public Sample sample(final int i, final int j) {
        final double[] rise = rise(i, j);
        return new Sample(xs[i], zs[j], heights[j][i], Math.hypot(rise[0], rise[1]));
    }

    /** From {@code low} to {@code high}, {@code every} apart, the last at {@code high}. */
    private double[] spaced(final double low, final double high, final double every) {
        final int count = (int) Math.ceil((high - low) / every) + 1;
        final double[] spaced = new double[count];
        for (int k = 0; k < count; k++) {
            spaced[k] = Math.min(low + k * every, high);
        }
        return spaced;
    }
}

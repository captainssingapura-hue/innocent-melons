package melons.innocent.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The lines a level runs along over heights on a grid, by marching squares: each square of four points cut where its
 * edges cross the level, a point on each such edge as far along it as the level is between the heights at its ends,
 * and the cuts of neighbouring squares joined edge to edge into loops. Beyond the grid the ground is taken as below
 * every level, so every loop closes: one running off the grid closes half a square outside it. A square whose
 * corners are above and below by turns is told which way to join by the average of its four. What is above the level
 * lies on one side of every loop, what is below on the other.
 */
final class Marching {

    private final double[][] heights;
    private final double x0;
    private final double z0;
    private final double every;
    private final int across;
    private final int along;

    /** Over {@code heights}, row by row along, the first point at ({@code x0}, {@code z0}), {@code every} apart. */
    Marching(final double[][] heights, final double x0, final double z0, final double every) {
        this.heights = heights;
        this.x0 = x0;
        this.z0 = z0;
        this.every = every;
        this.along = heights.length;
        this.across = heights[0].length;
    }

    /** The loops {@code level} runs along, each as its points in order round it. */
    List<List<Spot>> loops(final double level) {
        final Map<Long, long[]> joined = new HashMap<>();
        for (int j = -1; j < along; j++) {
            for (int i = -1; i < across; i++) {
                cut(i, j, level, joined);
            }
        }
        final List<List<Spot>> loops = new ArrayList<>();
        while (!joined.isEmpty()) {
            final long first = joined.keySet().iterator().next();
            final List<Spot> loop = new ArrayList<>();
            long previous = -1;
            long at = first;
            do {
                loop.add(point(at, level));
                final long[] ends = joined.remove(at);
                final long next = ends[0] == previous ? ends[1] : ends[0];
                previous = at;
                at = next;
            } while (at != first && joined.containsKey(at));
            if (loop.size() >= 3) {
                loops.add(loop);
            }
        }
        return loops;
    }

    /** Square ({@code i}, {@code j}), its near left corner there, cut where {@code level} crosses its edges. */
    private void cut(final int i, final int j, final double level, final Map<Long, long[]> joined) {
        final boolean a = height(i, j) > level;
        final boolean b = height(i + 1, j) > level;
        final boolean c = height(i + 1, j + 1) > level;
        final boolean d = height(i, j + 1) > level;
        final long near = edge(i, j, false);
        final long right = edge(i + 1, j, true);
        final long far = edge(i, j + 1, false);
        final long left = edge(i, j, true);
        final int corners = (a ? 1 : 0) | (b ? 2 : 0) | (c ? 4 : 0) | (d ? 8 : 0);
        switch (corners) {
            case 0, 15 -> { }
            case 1, 14 -> join(left, near, joined);
            case 2, 13 -> join(near, right, joined);
            case 3, 12 -> join(left, right, joined);
            case 4, 11 -> join(right, far, joined);
            case 6, 9 -> join(near, far, joined);
            case 7, 8 -> join(left, far, joined);
            default -> {
                final double middle = (height(i, j) + height(i + 1, j) + height(i + 1, j + 1) + height(i, j + 1)) / 4;
                if ((middle > level) == (corners == 5)) {
                    join(left, far, joined);
                    join(near, right, joined);
                } else {
                    join(left, near, joined);
                    join(right, far, joined);
                }
            }
        }
    }

    /** {@code one} and {@code other}, points on two edges, joined. */
    private void join(final long one, final long other, final Map<Long, long[]> joined) {
        add(one, other, joined);
        add(other, one, joined);
    }

    private void add(final long from, final long to, final Map<Long, long[]> joined) {
        final long[] ends = joined.computeIfAbsent(from, key -> new long[] {-1, -1});
        ends[ends[0] < 0 ? 0 : 1] = to;
    }

    /** The edge from point ({@code i}, {@code j}), along if {@code up}, else across, as one number. */
    private long edge(final int i, final int j, final boolean up) {
        return (((long) (j + 1) * (across + 2) + (i + 1)) << 1) | (up ? 1 : 0);
    }

    /** Where on edge {@code edge} the level crosses it. */
    private Spot point(final long edge, final double level) {
        final boolean up = (edge & 1) == 1;
        final long cell = edge >> 1;
        final int i = (int) (cell % (across + 2)) - 1;
        final int j = (int) (cell / (across + 2)) - 1;
        final int i2 = up ? i : i + 1;
        final int j2 = up ? j + 1 : j;
        final double one = height(i, j);
        final double other = height(i2, j2);
        final double share = Double.isInfinite(one) || Double.isInfinite(other) ? 0.5 : (level - one) / (other - one);
        return new Spot(x0 + (i + (i2 - i) * share) * every, z0 + (j + (j2 - j) * share) * every);
    }

    /** The height at point ({@code i}, {@code j}); below every level, off the grid. */
    private double height(final int i, final int j) {
        return i < 0 || j < 0 || i >= across || j >= along ? Double.NEGATIVE_INFINITY : heights[j][i];
    }
}

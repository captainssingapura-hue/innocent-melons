package melons.innocent.terrain.tiles;

import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * A tile's triangles, each with what it is sorted into: two to a square, the first by the square's near left, far left
 * and near right points and the second by its near right, far left and far right, each wound to face up. A triangle
 * whose corners are not all sorted alike is cut where they part: along each of its edges, where what lies there stops
 * being what lies at the edge's first end, found by halving {@link #HALVINGS} times, the ground there sampled afresh;
 * and, where all three of its corners differ, at its middle too. Each piece goes with the corner it holds. A point a
 * cut adds lies on the edge it cuts, as high as the edge is there, so the ground's shape is not changed and no crack
 * opens: only where one thing ends and the next begins. A point is either one of the tile's own, numbered as it
 * numbers them, or one a cut adds, numbered after them, made of up to three of the tile's own, each in a share.
 */
public final class Triangles {

    /** How many times an edge is halved to find where what lies along it changes: to a thousandth of it. */
    public static final int HALVINGS = 10;
    private static final double THIRD = 1.0 / 3;

    private final int own;
    private final List<Integer> corners = new ArrayList<>();
    private final List<Integer> sorts = new ArrayList<>();
    private final List<int[]> froms = new ArrayList<>();
    private final List<double[]> shares = new ArrayList<>();
    private final Map<Long, Integer> crossings = new HashMap<>();

    /** {@code tile}'s triangles, cut where what lies on them, as {@code sorted} says of {@code ground}, parts. */
    public Triangles(final TileData tile, final Ground ground, final ToIntFunction<Sample> sorted) {
        final int n = tile.points();
        this.own = n * n;
        for (int j = 0; j + 1 < n; j++) {
            for (int i = 0; i + 1 < n; i++) {
                final int near = j * n + i;
                final int far = near + n;
                laid(tile, ground, sorted, new int[] {near, far, near + 1});
                laid(tile, ground, sorted, new int[] {near + 1, far, far + 1});
            }
        }
    }

    /** How many triangles there are. */
    public int count() {
        return sorts.size();
    }

    /** Corner {@code k} (0, 1 or 2) of triangle {@code t}: one of the tile's own points, or one a cut added. */
    public int corner(final int t, final int k) {
        return corners.get(3 * t + k);
    }

    /** What triangle {@code t} is sorted into. */
    public int sort(final int t) {
        return sorts.get(t);
    }

    /** How many points the tile has of its own, numbered before those cuts add. */
    public int own() {
        return own;
    }

    /** How many points cuts add. */
    public int added() {
        return froms.size();
    }

    /** Which of the tile's own points added point {@code a} is made of, the {@code k}th (0, 1 or 2). */
    public int from(final int a, final int k) {
        return froms.get(a)[k];
    }

    /** How much of it the {@code k}th of added point {@code a}'s points makes. */
    public double share(final int a, final int k) {
        return shares.get(a)[k];
    }

    /** Triangle {@code v} laid whole, or cut where what lies at its corners parts, each piece wound as it is. */
    private void laid(final TileData tile, final Ground ground, final ToIntFunction<Sample> sorted, final int[] v) {
        final int[] s = {sortAt(tile, v[0]), sortAt(tile, v[1]), sortAt(tile, v[2])};
        if (s[0] == s[1] && s[1] == s[2]) {
            emit(v[0], v[1], v[2], s[0]);
            return;
        }
        final boolean three = s[0] != s[1] && s[1] != s[2] && s[0] != s[2];
        final int middle = three ? add(new int[] {v[0], v[1], v[2]}, new double[] {THIRD, THIRD, THIRD}) : -1;
        final Map<Integer, List<Integer>> pieces = new HashMap<>();
        for (int k = 0; k < 3; k++) {
            final int a = v[k];
            final int b = v[(k + 1) % 3];
            pieces.computeIfAbsent(s[k], x -> new ArrayList<>()).add(a);
            if (s[k] != s[(k + 1) % 3]) {
                final int cut = crossing(tile, ground, sorted, a, b);
                pieces.get(s[k]).add(cut);
                if (three) {
                    pieces.get(s[k]).add(middle);
                }
                pieces.computeIfAbsent(s[(k + 1) % 3], x -> new ArrayList<>()).add(cut);
            }
        }
        pieces.forEach((sort, ring) -> {
            for (int k = 1; k + 1 < ring.size(); k++) {
                emit(ring.get(0), ring.get(k), ring.get(k + 1), sort);
            }
        });
    }

    /** Where along the edge from point {@code a} to {@code b} what lies there parts, the same whichever way asked. */
    private int crossing(final TileData tile, final Ground ground, final ToIntFunction<Sample> sorted, final int a,
                         final int b) {
        final int lo = Math.min(a, b);
        final int hi = Math.max(a, b);
        return crossings.computeIfAbsent((long) lo * own + hi, key -> {
            final int n = tile.points();
            final int first = sortAt(tile, lo);
            double from = 0;
            double to = 1;
            for (int h = 0; h < HALVINGS; h++) {
                final double t = (from + to) / 2;
                final double x = tile.x(lo % n) * (1 - t) + tile.x(hi % n) * t;
                final double z = tile.z(lo / n) * (1 - t) + tile.z(hi / n) * t;
                final double across = tile.across(lo % n, lo / n) * (1 - t) + tile.across(hi % n, hi / n) * t;
                final double along = tile.along(lo % n, lo / n) * (1 - t) + tile.along(hi % n, hi / n) * t;
                if (sorted.applyAsInt(new Sample(x, z, ground.height(x, z), Math.hypot(across, along))) == first) {
                    from = t;
                } else {
                    to = t;
                }
            }
            final double t = (from + to) / 2;
            return add(new int[] {lo, hi, hi}, new double[] {1 - t, t, 0});
        });
    }

    private int sortAt(final TileData tile, final int point) {
        return tile.sort(point % tile.points(), point / tile.points());
    }

    private int add(final int[] points, final double[] parts) {
        froms.add(points);
        shares.add(parts);
        return own + froms.size() - 1;
    }

    private void emit(final int a, final int b, final int c, final int sort) {
        corners.addAll(List.of(a, b, c));
        sorts.add(sort);
    }
}

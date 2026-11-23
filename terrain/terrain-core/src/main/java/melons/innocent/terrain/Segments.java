package melons.innocent.terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * The straight pieces of a line through points in order, closed round or not, filed by where they lie on the map so
 * what is near a spot is found without looking at the rest: in square cells, each piece in every cell the box round it
 * touches, for finding the nearest; and in rows, each piece in every row it crosses, for telling inside from outside.
 * The nearest is found by looking at the cell a spot is in, or the nearest cell to it, then at ring after ring of cells
 * round that, until no piece further out could be nearer than the nearest found. The same answers as looking at every
 * piece; worked out once, the same for whoever asks.
 */
final class Segments {

    /** How many pieces to a cell, on the average, about. */
    private static final int FILLED = 4;
    /** How many cells each way, at most. */
    private static final int MOST = 256;

    private final double[] xs;
    private final double[] zs;
    private final int pieces;
    private final double x0;
    private final double z0;
    private final double size;
    private final int across;
    private final int along;
    private final int[][] cells;
    private final int[][] rows;

    /** The pieces through ({@code xs}, {@code zs}) in order, the last joined back to the first if {@code closed}. */
    Segments(final double[] xs, final double[] zs, final boolean closed) {
        this.xs = xs;
        this.zs = zs;
        this.pieces = closed ? xs.length : xs.length - 1;
        double minX = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        double length = 0;
        for (int k = 0; k < pieces; k++) {
            minX = Math.min(minX, xs[k]);
            minZ = Math.min(minZ, zs[k]);
            maxX = Math.max(maxX, xs[k]);
            maxZ = Math.max(maxZ, zs[k]);
            length += Math.hypot(xs[next(k)] - xs[k], zs[next(k)] - zs[k]);
        }
        minX = Math.min(minX, xs[xs.length - 1]);
        minZ = Math.min(minZ, zs[zs.length - 1]);
        maxX = Math.max(maxX, xs[xs.length - 1]);
        maxZ = Math.max(maxZ, zs[zs.length - 1]);
        this.x0 = minX;
        this.z0 = minZ;
        final double wide = Math.max(maxX - minX, maxZ - minZ);
        this.size = Math.max(Math.max(FILLED * length / Math.max(1, pieces), wide / MOST), 1);
        this.across = Math.max(1, (int) Math.ceil((maxX - minX) / size) + 1);
        this.along = Math.max(1, (int) Math.ceil((maxZ - minZ) / size) + 1);
        this.cells = filed(true);
        this.rows = filed(false);
    }

    /** Each cell's pieces, or each row's: every piece in every cell its box touches, or every row it crosses. */
    private int[][] filed(final boolean inCells) {
        final List<List<Integer>> filed = new ArrayList<>();
        for (int c = 0; c < (inCells ? across * along : along); c++) {
            filed.add(new ArrayList<>());
        }
        for (int k = 0; k < pieces; k++) {
            final int j0 = row(Math.min(zs[k], zs[next(k)]));
            final int j1 = row(Math.max(zs[k], zs[next(k)]));
            final int i0 = column(Math.min(xs[k], xs[next(k)]));
            final int i1 = column(Math.max(xs[k], xs[next(k)]));
            for (int j = j0; j <= j1; j++) {
                if (inCells) {
                    for (int i = i0; i <= i1; i++) {
                        filed.get(j * across + i).add(k);
                    }
                } else {
                    filed.get(j).add(k);
                }
            }
        }
        final int[][] out = new int[filed.size()][];
        for (int c = 0; c < out.length; c++) {
            out[c] = filed.get(c).stream().mapToInt(Integer::intValue).toArray();
        }
        return out;
    }

    /** The point after point {@code k}, round to the first if closed. */
    private int next(final int k) {
        return k + 1 == xs.length ? 0 : k + 1;
    }

    private int column(final double x) {
        return Math.clamp((int) Math.floor((x - x0) / size), 0, across - 1);
    }

    private int row(final double z) {
        return Math.clamp((int) Math.floor((z - z0) / size), 0, along - 1);
    }

    /**
     * The piece nearest ({@code x}, {@code z}) and where on it: {@code into[0]} how far off, {@code into[1]} which
     * piece, {@code into[2]} how far along it, from nothing at its first point to one at its second.
     */
    void nearest(final double x, final double z, final double[] into) {
        final int ci = column(x);
        final int cj = row(z);
        into[0] = Double.MAX_VALUE;
        into[1] = 0;
        into[2] = 0;
        for (int ring = 0; ring <= Math.max(across, along); ring++) {
            for (int j = Math.max(0, cj - ring); j <= Math.min(along - 1, cj + ring); j++) {
                if (j == cj - ring || j == cj + ring) {
                    for (int i = Math.max(0, ci - ring); i <= Math.min(across - 1, ci + ring); i++) {
                        looked(i, j, x, z, into);
                    }
                } else {
                    looked(ci - ring, j, x, z, into);
                    looked(ci + ring, j, x, z, into);
                }
            }
            if (into[0] < Double.MAX_VALUE && Math.sqrt(into[0]) <= ring * size) {
                break;
            }
        }
        into[0] = Math.sqrt(into[0]);
    }

    /**
     * The pieces in cell ({@code i}, {@code j}), if there is such a cell, looked at for one nearer ({@code x},
     * {@code z}) than the nearest so far: {@code into} as {@link #nearest} has it, but how far off squared.
     */
    private void looked(final int i, final int j, final double x, final double z, final double[] into) {
        if (i < 0 || i >= across) {
            return;
        }
        for (final int k : cells[j * across + i]) {
            final double dx = xs[next(k)] - xs[k];
            final double dz = zs[next(k)] - zs[k];
            final double square = dx * dx + dz * dz;
            final double t = square == 0 ? 0 : Math.clamp(((x - xs[k]) * dx + (z - zs[k]) * dz) / square, 0, 1);
            final double ex = x - xs[k] - t * dx;
            final double ez = z - zs[k] - t * dz;
            final double off = ex * ex + ez * ez;
            if (off < into[0]) {
                into[0] = off;
                into[1] = k;
                into[2] = t;
            }
        }
    }

    /** Whether ({@code x}, {@code z}) is inside them, closed round: whether a line on across crosses them oddly. */
    boolean inside(final double x, final double z) {
        if (z < z0 || z > z0 + along * size) {
            return false;
        }
        boolean inside = false;
        for (final int k : rows[row(z)]) {
            final int n = next(k);
            if (zs[k] > z != zs[n] > z && x < xs[k] + (z - zs[k]) * (xs[n] - xs[k]) / (zs[n] - zs[k])) {
                inside = !inside;
            }
        }
        return inside;
    }
}

package melons.innocent.terrain.bench;

import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileKey;
import melons.innocent.terrain.tiles.Tiling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * What of the land the eye can see, as a shape on the map: where the four corners of its view meet the sea's level,
 * with the spot straight under it, and all between them. Ground higher than the sea is met sooner along each line of
 * sight, so between the spot under the eye and those corners; none of the land seen lies outside. A line of sight that
 * never comes down to the sea is taken as far as {@link #FURTHEST} times the eye's height. And the tiles of one level
 * that lie over it, all alike, none coarser than its neighbour, the nearest to the spot under the eye first; and those
 * it would see turned any way round.
 */
public final class Footprint {

    /** How far a line of sight that never comes down is taken, as so many times the eye's height. */
    private static final double FURTHEST = 20;

    private final double underX;
    private final double underZ;
    private final List<double[]> hull;

    /** What {@code flight}'s eye sees, its view {@code field} degrees high and {@code aspect} times as wide. */
    public Footprint(final Flight flight, final double field, final double aspect) {
        this.underX = flight.x();
        this.underZ = flight.z();
        final double yaw = Math.toRadians(flight.yaw());
        final double pitch = Math.toRadians(flight.pitch());
        final double[] ahead = {Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch)};
        final double[] right = {Math.cos(yaw), 0, -Math.sin(yaw)};
        final double[] up = {ahead[1] * right[2] - ahead[2] * right[1], ahead[2] * right[0] - ahead[0] * right[2],
            ahead[0] * right[1] - ahead[1] * right[0]};
        final double high = Math.tan(Math.toRadians(field) / 2);
        final double wide = high * aspect;
        final List<double[]> points = new ArrayList<>(List.of(new double[] {underX, underZ}));
        for (final int a : new int[] {-1, 1}) {
            for (final int b : new int[] {-1, 1}) {
                final double[] d = new double[3];
                for (int q = 0; q < 3; q++) {
                    d[q] = ahead[q] + a * wide * right[q] + b * high * up[q];
                }
                final double level = Math.hypot(d[0], d[2]);
                final double t = d[1] < 0 ? Math.min(flight.height() / -d[1], FURTHEST * flight.height() / level)
                        : FURTHEST * flight.height() / level;
                points.add(new double[] {underX + t * d[0], underZ + t * d[2]});
            }
        }
        this.hull = hull(points);
    }

    /** The tiles of {@code tiling} at {@code level} that lie over it, the nearest to the spot under the eye first. */
    public List<Chosen> tiles(final Tiling tiling, final int level) {
        final double side = tiling.side(level);
        final int count = new TileKey(level, 0, 0).across();
        double minX = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (final double[] p : hull) {
            minX = Math.min(minX, p[0]);
            minZ = Math.min(minZ, p[1]);
            maxX = Math.max(maxX, p[0]);
            maxZ = Math.max(maxZ, p[1]);
        }
        final List<Chosen> tiles = new ArrayList<>();
        final int i0 = Math.max(0, (int) Math.floor((minX - tiling.x0()) / side));
        final int i1 = Math.min(count - 1, (int) Math.floor((maxX - tiling.x0()) / side));
        final int j0 = Math.max(0, (int) Math.floor((minZ - tiling.z0()) / side));
        final int j1 = Math.min(count - 1, (int) Math.floor((maxZ - tiling.z0()) / side));
        for (int j = j0; j <= j1; j++) {
            for (int i = i0; i <= i1; i++) {
                final double cx = tiling.x0() + (i + 0.5) * side;
                final double cz = tiling.z0() + (j + 0.5) * side;
                if (distance(cx, cz) <= side * Math.sqrt(0.5)) {
                    tiles.add(new Chosen(new TileKey(level, i, j), 0));
                }
            }
        }
        tiles.sort(Comparator.comparingDouble(tile -> off(tiling, side, tile.key().i(), tile.key().j())));
        return tiles;
    }

    /**
     * The tiles of {@code tiling} at {@code level} over what the eye would see turned any way round, as far off as it
     * sees now: within a circle round the spot under it, the nearest first.
     */
    public List<TileKey> round(final Tiling tiling, final int level) {
        final double side = tiling.side(level);
        final int count = new TileKey(level, 0, 0).across();
        double reach = 0;
        for (final double[] p : hull) {
            reach = Math.max(reach, Math.hypot(p[0] - underX, p[1] - underZ));
        }
        reach += side * Math.sqrt(0.5);
        final List<TileKey> keys = new ArrayList<>();
        final int i0 = Math.max(0, (int) Math.floor((underX - reach - tiling.x0()) / side));
        final int i1 = Math.min(count - 1, (int) Math.floor((underX + reach - tiling.x0()) / side));
        final int j0 = Math.max(0, (int) Math.floor((underZ - reach - tiling.z0()) / side));
        final int j1 = Math.min(count - 1, (int) Math.floor((underZ + reach - tiling.z0()) / side));
        for (int j = j0; j <= j1; j++) {
            for (int i = i0; i <= i1; i++) {
                if (off(tiling, side, i, j) <= reach) {
                    keys.add(new TileKey(level, i, j));
                }
            }
        }
        keys.sort(Comparator.comparingDouble(key -> off(tiling, side, key.i(), key.j())));
        return keys;
    }

    /** How far the middle of tile ({@code i}, {@code j}), {@code side} across, is from the spot under the eye. */
    private double off(final Tiling tiling, final double side, final int i, final int j) {
        return Math.hypot(tiling.x0() + (i + 0.5) * side - underX, tiling.z0() + (j + 0.5) * side - underZ);
    }

    /** How far ({@code x}, {@code z}) is from it: nothing, inside it. */
    double distance(final double x, final double z) {
        boolean inside = true;
        double least = Double.MAX_VALUE;
        for (int k = 0; k < hull.size(); k++) {
            final double[] a = hull.get(k);
            final double[] b = hull.get((k + 1) % hull.size());
            final double dx = b[0] - a[0];
            final double dz = b[1] - a[1];
            inside &= dx * (z - a[1]) - dz * (x - a[0]) >= 0;
            final double square = dx * dx + dz * dz;
            final double t = square == 0 ? 0 : Math.clamp(((x - a[0]) * dx + (z - a[1]) * dz) / square, 0, 1);
            least = Math.min(least, Math.hypot(x - a[0] - t * dx, z - a[1] - t * dz));
        }
        return inside ? 0 : least;
    }

    /** The smallest shape with no dent that holds {@code points}, round it the way that keeps it on the left. */
    private List<double[]> hull(final List<double[]> points) {
        final List<double[]> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.<double[]>comparingDouble(p -> p[0]).thenComparingDouble(p -> p[1]));
        final List<double[]> round = new ArrayList<>();
        for (int pass = 0; pass < 2; pass++) {
            final int start = round.size();
            for (final double[] p : pass == 0 ? sorted : sorted.reversed()) {
                while (round.size() >= start + 2 && turn(round.get(round.size() - 2), round.getLast(), p) <= 0) {
                    round.removeLast();
                }
                round.add(p);
            }
            round.removeLast();
        }
        return List.copyOf(round);
    }

    /** How far {@code c} turns left of the way from {@code a} to {@code b}. */
    private double turn(final double[] a, final double[] b, final double[] c) {
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
    }
}

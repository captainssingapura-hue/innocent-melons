package melons.innocent.terrain.tiles;

import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * What lies at each point of a tile, sampled from the land: how high the ground is, how it rises across and along,
 * and which layer of a painting, or of what lies on it, a point is in, as {@code sorted} says of the ground there.
 * How high water's top is at each point, where water reaches, is kept too. Its points are its tiling's cells plus one
 * to a side, the first at its near left corner; a ring of points a step beyond its edges is sampled too, for how the
 * ground rises at its edges, and not kept. Its triangles are cut where what lies on them parts ({@link Triangles}).
 * Worked out once, the same from the same land every time; never changed.
 */
public final class TileData implements Sampled {

    private final TileKey key;
    private final double x0;
    private final double z0;
    private final double spacing;
    private final int points;
    private final double[] heights;
    private final double[] across;
    private final double[] along;
    private final int[] sorts;
    private final double[] waters;
    private final Triangles triangles;

    /** {@code key}'s tile of {@code tiling}, sampled from {@code ground}, each point sorted as {@code sorted} says. */
    public TileData(final Tiling tiling, final TileKey key, final Ground ground, final ToIntFunction<Sample> sorted) {
        this(tiling, key, ground, sorted, at -> Double.NaN);
    }

    /**
     * The same, and how high the water's top is at each point, as {@code water} says of the ground there: not a number
     * where no water reaches.
     */
    public TileData(final Tiling tiling, final TileKey key, final Ground ground, final ToIntFunction<Sample> sorted,
                    final ToDoubleFunction<Sample> water) {
        this.key = key;
        this.x0 = tiling.x0(key);
        this.z0 = tiling.z0(key);
        this.spacing = tiling.spacing(key.level());
        this.points = tiling.cells() + 1;
        final int ringed = points + 2;
        final double[] ring = new double[ringed * ringed];
        for (int j = 0; j < ringed; j++) {
            for (int i = 0; i < ringed; i++) {
                ring[j * ringed + i] = ground.height(x0 + (i - 1) * spacing, z0 + (j - 1) * spacing);
            }
        }
        this.heights = new double[points * points];
        this.across = new double[points * points];
        this.along = new double[points * points];
        this.sorts = new int[points * points];
        this.waters = new double[points * points];
        for (int j = 0; j < points; j++) {
            for (int i = 0; i < points; i++) {
                final int at = (j + 1) * ringed + i + 1;
                final int p = j * points + i;
                heights[p] = ring[at];
                across[p] = (ring[at + 1] - ring[at - 1]) / (2 * spacing);
                along[p] = (ring[at + ringed] - ring[at - ringed]) / (2 * spacing);
                final Sample sample = new Sample(x(i), z(j), heights[p], Math.hypot(across[p], along[p]));
                sorts[p] = sorted.applyAsInt(sample);
                waters[p] = water.applyAsDouble(sample);
            }
        }
        this.triangles = new Triangles(this, sorted);
    }

    /** Which tile it is. */
    public TileKey key() {
        return key;
    }

    /** How many points it has to a side. */
    public int points() {
        return points;
    }

    /** How far apart its points are. */
    public double spacing() {
        return spacing;
    }

    /** Where it begins across. */
    public double x0() {
        return x0;
    }

    /** Where it begins along. */
    public double z0() {
        return z0;
    }

    @Override
    public int columns() {
        return points;
    }

    @Override
    public int rows() {
        return points;
    }

    /** How far across point column {@code i} is. */
    public double x(final int i) {
        return x0 + i * spacing;
    }

    /** How far along point row {@code j} is. */
    public double z(final int j) {
        return z0 + j * spacing;
    }

    /** How high the ground is at point ({@code i}, {@code j}). */
    public double height(final int i, final int j) {
        return heights[j * points + i];
    }

    /** How far the ground rises across, for each unit across, at point ({@code i}, {@code j}). */
    public double across(final int i, final int j) {
        return across[j * points + i];
    }

    /** How far the ground rises along, for each unit along, at point ({@code i}, {@code j}). */
    public double along(final int i, final int j) {
        return along[j * points + i];
    }

    /** Its triangles, cut where what lies on them parts. */
    public Triangles triangles() {
        return triangles;
    }

    /** How high the water's top is at point ({@code i}, {@code j}): not a number where no water reaches. */
    public double water(final int i, final int j) {
        return waters[j * points + i];
    }

    /** What point ({@code i}, {@code j}) was sorted into. */
    public int sort(final int i, final int j) {
        return sorts[j * points + i];
    }
}

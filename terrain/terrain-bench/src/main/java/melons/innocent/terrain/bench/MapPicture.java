package melons.innocent.terrain.bench;

import melons.innocent.terrain.drawn.Painting;

import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;

import java.util.List;
import java.util.stream.IntStream;

/**
 * A small map of the land, north up, {@code size} pixels each way over its extent: each pixel in the colour the
 * painting's legend shows for its topmost layer that lies there, the ground there sampled as a tile samples it, or
 * for its water, where the painting draws water over the ground and the ground there is under it; lit
 * as the ground slopes there from the north-west, so the hills show. Worked out once, its rows side by side; never
 * changed. Pixels as a picture writes them, alpha, red, green, blue.
 */
public final class MapPicture {

    /** How much a slope facing the light lightens, or one facing away darkens, for each unit it rises. */
    private static final double LIT = 6;
    private static final double DARKEST = 0.55;
    private static final double LIGHTEST = 1.25;
    private static final int OPAQUE = 0xff000000;
    private static final int FULL = 255;

    private final Extent extent;
    private final int size;
    private final int[] pixels;

    /** The map of {@code ground}, painted as {@code painting} says, {@code size} pixels each way. */
    public MapPicture(final Ground ground, final Painting painting, final int size) {
        this.extent = ground.extent();
        this.size = size;
        this.pixels = new int[size * size];
        final List<Painting.Shown> legend = painting.legend();
        final double step = Math.max(extent.across(), extent.along()) / size;
        IntStream.range(0, size).parallel().forEach(row -> {
            for (int column = 0; column < size; column++) {
                final double x = x(column);
                final double z = z(row);
                final double west = ground.height(x - step, z);
                final double east = ground.height(x + step, z);
                final double north = ground.height(x, z + step);
                final double south = ground.height(x, z - step);
                final double light = Math.clamp(1 + (west - east + north - south) / (2 * step) * LIT, DARKEST,
                        LIGHTEST);
                final Sample at = new Sample(x, z, ground.height(x, z),
                        Math.hypot(east - west, north - south) / (2 * step));
                final double water = painting.water(at);
                final int layer = !Double.isNaN(water) && water > at.height() && painting.waterLayer() >= 0
                        ? painting.waterLayer() : topmost(painting, legend.size(), at);
                final Painting.Shown shown = legend.get(layer);
                pixels[row * size + column] = OPAQUE | channel(shown.red(), light) << 16
                        | channel(shown.green(), light) << 8 | channel(shown.blue(), light);
            }
        });
    }

    /** How many pixels it is each way. */
    public int size() {
        return size;
    }

    /** Its pixels, row by row from the top, the north. */
    public int[] pixels() {
        return pixels.clone();
    }

    /** How far across the map pixel column {@code column}'s middle is. */
    public double x(final double column) {
        return extent.x0() + (column + 0.5) * Math.max(extent.across(), extent.along()) / size;
    }

    /** How far along the map pixel row {@code row}'s middle is, the top row the furthest north. */
    public double z(final double row) {
        return extent.z0() + (size - row - 0.5) * Math.max(extent.across(), extent.along()) / size;
    }

    /** Which pixel column, with its fraction, lies at {@code x} across. */
    public double column(final double x) {
        return (x - extent.x0()) * size / Math.max(extent.across(), extent.along()) - 0.5;
    }

    /** Which pixel row, with its fraction, lies at {@code z} along. */
    public double row(final double z) {
        return size - (z - extent.z0()) * size / Math.max(extent.across(), extent.along()) - 0.5;
    }

    /** The painting's topmost layer that lies at {@code at}, of its {@code layers}: its first, if no other does. */
    private int topmost(final Painting painting, final int layers, final Sample at) {
        for (int layer = layers - 1; layer > 0; layer--) {
            if (painting.covers(layer, at)) {
                return layer;
            }
        }
        return 0;
    }

    private int channel(final double share, final double light) {
        return (int) Math.round(Math.clamp(share * light, 0, 1) * FULL);
    }
}

package melons.innocent.terrain;

/**
 * The ground over a stretch of the map: how high it is at any point in its extent, the same every time. What any
 * realizer reads of the land, whatever it is made of: the card draws it from this, and in time the simulation will
 * stand on it. In centimetres: across (x) and along (z) the map, up (y) from the sea's level.
 */
public interface Ground {

    /** The stretch of the map it covers. */
    Extent extent();

    /** How high it is at ({@code x}, {@code z}), a point in its extent. */
    double height(double x, double z);

    /**
     * How high it is at each point of a grid, {@code across} points to a row and {@code along} rows, {@code spacing}
     * apart, the first at ({@code x0}, {@code z0}): row by row from the nearest, each from the left. As high as
     * {@link #height} says at each; a ground that can work out a grid more cheaply all at once may.
     */
    default double[] heights(final double x0, final double z0, final double spacing, final int across,
                             final int along) {
        final double[] heights = new double[across * along];
        for (int j = 0; j < along; j++) {
            for (int i = 0; i < across; i++) {
                heights[j * across + i] = height(x0 + i * spacing, z0 + j * spacing);
            }
        }
        return heights;
    }
}

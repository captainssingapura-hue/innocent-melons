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
}

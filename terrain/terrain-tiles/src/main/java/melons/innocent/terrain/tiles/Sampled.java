package melons.innocent.terrain.tiles;

/**
 * Points sampled from the ground in rows and columns, each sorted into what lies there: a tile, or a whole sheet of the
 * land. How far across and along each is, how high the ground is there and how it rises, and what it is sorted into.
 * Numbered row by row from the nearest, each row from the left: point ({@code i}, {@code j}) is number
 * {@code j * columns() + i}. What {@link Triangles} cuts.
 */
public interface Sampled {

    /** How many points each row has. */
    int columns();

    /** How many rows there are. */
    int rows();

    /** How far across point column {@code i} is. */
    double x(int i);

    /** How far along point row {@code j} is. */
    double z(int j);

    /** How high the ground is at point ({@code i}, {@code j}). */
    double height(int i, int j);

    /** How far the ground rises across, for each unit across, at point ({@code i}, {@code j}). */
    double across(int i, int j);

    /** How far the ground rises along, for each unit along, at point ({@code i}, {@code j}). */
    double along(int i, int j);

    /** What point ({@code i}, {@code j}) is sorted into. */
    int sort(int i, int j);
}

package melons.innocent.core.sheet;

/**
 * A sheet's shape across the line it is swept along: how high it is over the line's own height at each distance to
 * the line's right, from one edge to the other. Flat and tilted, it is a road's crossfall or a banked bend; an arc,
 * a vault or a dome.
 */
public interface Profile {

    /** Its left edge and its right: how far to the line's right each is, less than nothing being to its left. */
    double from();

    double to();

    /** How high it is over the line's height, so far {@code aside}, in centimetres. */
    double high(double aside);

    /** How much higher it is a centimetre further to the right, there. */
    double rises(double aside);
}

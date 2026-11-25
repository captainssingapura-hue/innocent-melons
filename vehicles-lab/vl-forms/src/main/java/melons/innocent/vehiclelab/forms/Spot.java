package melons.innocent.vehiclelab.forms;

/**
 * A spot on a flat thing, as a drawing of a pressing has it: {@code u} along the drawing, {@code v} up it.
 *
 * @param u how far along
 * @param v how far up
 */
public record Spot(double u, double v) {

    /** The way from this spot to {@code other}. */
    public Spot to(final Spot other) {
        return new Spot(other.u - u, other.v - v);
    }

    /** How much {@code a} turns to {@code b}, both from here: more than nought if left, as a drawing turns. */
    public double turn(final Spot a, final Spot b) {
        return (a.u - u) * (b.v - v) - (a.v - v) * (b.u - u);
    }

    /** How far it is to {@code other}. */
    public double distance(final Spot other) {
        return Math.hypot(other.u - u, other.v - v);
    }
}

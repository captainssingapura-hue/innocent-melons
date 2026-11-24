package melons.innocent.core.motion;

/**
 * A spot in the world, or a way and a distance from one: so far across, so far up and so far along, in centimetres.
 * Up is up: whoever counts height downward turns it over before telling it here.
 *
 * @param across how far across
 * @param up     how far up
 * @param along  how far along
 */
public record Spot(double across, double up, double along) {

    public Spot {
        if (!Double.isFinite(across) || !Double.isFinite(up) || !Double.isFinite(along)) {
            throw new IllegalArgumentException("a spot somewhere: " + across + ", " + up + ", " + along);
        }
    }

    /** This and {@code other} together. */
    public Spot plus(final Spot other) {
        return new Spot(across + other.across, up + other.up, along + other.along);
    }

    /** From {@code other} to this. */
    public Spot minus(final Spot other) {
        return new Spot(across - other.across, up - other.up, along - other.along);
    }

    /** This, so many {@code times} as far. */
    public Spot times(final double times) {
        return new Spot(across * times, up * times, along * times);
    }

    /** How far it is from where nothing is. */
    public double length() {
        return Math.sqrt(across * across + up * up + along * along);
    }

    /** What stands square to both this and {@code other}, as long as the two of them span. */
    public Spot cross(final Spot other) {
        return new Spot(up * other.along - along * other.up, along * other.across - across * other.along,
                across * other.up - up * other.across);
    }
}

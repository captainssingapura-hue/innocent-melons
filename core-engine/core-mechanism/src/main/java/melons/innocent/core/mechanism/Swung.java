package melons.innocent.core.mechanism;

/**
 * How a thing on a hinge stands at a tick: how far it has swung open from shut, and how fast it is turning, opening
 * counted upward.
 *
 * @param degrees how far open it is, in degrees from shut
 * @param turning how fast it is turning, in degrees a second; less than nothing, shutting
 */
public record Swung(double degrees, double turning) {

    public Swung {
        if (!Double.isFinite(degrees) || !Double.isFinite(turning)) {
            throw new IllegalArgumentException("swung some way, turning somehow: " + degrees + ", " + turning);
        }
    }

    /** Shut, and still. */
    public Swung() {
        this(0, 0);
    }

    /** Whether it is turning at all. */
    public boolean moving() {
        return turning != 0;
    }
}

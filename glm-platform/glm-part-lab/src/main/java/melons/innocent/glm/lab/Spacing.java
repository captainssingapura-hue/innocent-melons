package melons.innocent.glm.lab;

/**
 * How finely the grid is ruled: every 10 cm, heavier every metre, for a whole axle or a car; or every centimetre,
 * heavier every 10 cm, to work one part fine. In centimetres.
 */
public enum Spacing {
    NORMAL("Every 10 cm, and every metre", 10, 100),
    FINE("Every centimetre, and every 10 cm", 1, 10);

    private final String title;
    private final double minor;
    private final double major;

    Spacing(final String title, final double minor, final double major) {
        this.title = title;
        this.minor = minor;
        this.major = major;
    }

    /** What it is called. */
    public String title() {
        return title;
    }

    /** How far apart its light lines are. */
    public double minor() {
        return minor;
    }

    /** How far apart its heavy lines are. */
    public double major() {
        return major;
    }
}

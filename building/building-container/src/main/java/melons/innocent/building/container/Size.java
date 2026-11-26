package melons.innocent.building.container;

/**
 * How long a shipping container is: twenty feet, or forty; every one {@link #WIDE} wide and, high cube, {@link #HIGH}
 * high. In centimetres.
 */
public enum Size {
    TWENTY(606), FORTY(1_219);

    /** How wide every container is, and how high. */
    public static final double WIDE = 244;
    public static final double HIGH = 290;

    private final double length;

    Size(final double length) {
        this.length = length;
    }

    /** How long it is. */
    public double length() {
        return length;
    }
}

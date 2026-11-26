package melons.innocent.vehiclelab.wheels;

import java.util.Locale;

/**
 * A wheel's size, as a tyre's sidewall says it: the tyre's width in millimetres, how tall its wall is as a share of
 * that, and the rim's diameter in inches; 225/50 R17, say. Any size at all, so long as each is more than nought: it is
 * for whatever vehicle takes it to hold it to its own range. Its lengths in centimetres.
 *
 * @param width  how wide the tyre is, in millimetres
 * @param aspect how tall its wall is, as a share of how wide, in hundredths
 * @param rim    how big the rim is across, in inches
 */
public record WheelSize(double width, double aspect, double rim) {

    /** An inch in centimetres; a millimetre in centimetres. */
    private static final double INCH = 2.54;
    private static final double MILLIMETRE = 0.1;

    public WheelSize {
        if (!(width > 0 && aspect > 0 && rim > 0)) {
            throw new IllegalArgumentException("a tyre some width, its wall some height, on a rim some size: "
                    + width + "/" + aspect + " R" + rim);
        }
    }

    /** How far the rim reaches from the axle to where the tyre sits on it. */
    public double rimRadius() {
        return rim * INCH / 2;
    }

    /** How wide the tyre is. */
    public double wide() {
        return width * MILLIMETRE;
    }

    /** How tall the tyre's wall is. */
    public double wall() {
        return wide() * aspect / 100;
    }

    /** How far the tyre reaches from the axle to the ground. */
    public double radius() {
        return rimRadius() + wall();
    }

    /** How a sidewall would say it: 225/50 R17. */
    public String said() {
        return String.format(Locale.ENGLISH, "%s/%s R%s", plain(width), plain(aspect), plain(rim));
    }

    private String plain(final double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }
}

package melons.innocent.glm.model;

import melons.innocent.vehiclelab.wheels.WheelSize;

/**
 * A car's figures on the platform, as far as its rear axle needs them, in centimetres: how far apart its axles are, how
 * far apart its back wheels are, middle to middle, and its tyres as a tyre's wall says them, how wide in millimetres,
 * how tall the wall is as a share of that, and the rim's size in inches. Within the platform's ranges, a large car's:
 * axles 2.6 m to 3.2 m apart, the back wheels 1.5 m to 1.75 m apart, tyres 225 mm to 335 mm wide, walls 30% to 65%,
 * rims 17 to 23 inches. A large SUV's to begin with, when nothing else is said: 2.99 m between its axles, its back
 * wheels 1.68 m apart, on 285/45 R20 tyres.
 *
 * @param wheelbase how far apart its axles are
 * @param track     how far apart its back wheels are, middle to middle
 * @param width     how wide its tyres are, in millimetres
 * @param aspect    how tall their walls are, as a share of how wide, in hundredths
 * @param rim       how big its rims are, in inches
 */
public record Proportions(double wheelbase, double track, double width, double aspect, double rim) {

    public Proportions {
        refused(wheelbase, 260, 320, "axles 2.6 m to 3.2 m apart");
        refused(track, 150, 175, "back wheels 1.5 m to 1.75 m apart");
        refused(width, 225, 335, "tyres 225 mm to 335 mm wide");
        refused(aspect, 30, 65, "tyre walls 30% to 65% of their width");
        refused(rim, 17, 23, "rims 17 to 23 inches");
    }

    /** A large SUV's. */
    public Proportions() {
        this(299.4, 167.9, 285, 45, 20);
    }

    /** Its wheels' size, held to the platform's range, as a tyre's sidewall says it. */
    public WheelSize wheel() {
        return new WheelSize(width, aspect, rim);
    }

    /** How far its tyres reach from their axle to the ground. */
    public double tyre() {
        return wheel().radius();
    }

    /** How far its rims reach from their axle to where the tyre sits. */
    public double rimRadius() {
        return wheel().rimRadius();
    }

    /** How tall a tyre's wall is. */
    public double wall() {
        return wheel().wall();
    }

    /** How far each back wheel's middle is from the car's. */
    public double halfTrack() {
        return track / 2;
    }

    /** How far behind the front axle the back one is. */
    public double back() {
        return -wheelbase;
    }

    private void refused(final double value, final double least, final double most, final String why) {
        if (!(value >= least && value <= most)) {
            throw new IllegalArgumentException(why + ", not " + value);
        }
    }
}

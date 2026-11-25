package melons.innocent.glm.model;

/**
 * A body's own figures, on top of the platform's, the ones that make it a body of one kind or another, in
 * centimetres: how far its nose reaches ahead of the front axle and its tail behind the rear axle; how high its
 * roof is over the ground; how wide it is over its sills; and where its windscreen's foot and its pillars stand, as
 * shares of the wheelbase behind the front axle. A large SUV's to begin with.
 *
 * @param nose   how far its nose reaches ahead of the front axle
 * @param tail   how far its tail reaches behind the rear axle
 * @param roof   how high its roof is over the ground
 * @param wide   how wide it is over its sills
 * @param cowl   where its windscreen's foot stands, behind the front axle, as a share of the wheelbase
 * @param pillar where its B pillar stands, behind the front axle, as a share of the wheelbase
 */
public record BodyFigures(double nose, double tail, double roof, double wide, double cowl, double pillar) {

    public BodyFigures {
        refused(nose, 60, 140, "a nose");
        refused(tail, 60, 150, "a tail");
        refused(roof, 120, 200, "a roof");
        refused(wide, 160, 220, "a width over the sills");
        refused(cowl, 0.12, 0.3, "a windscreen's foot");
        refused(pillar, 0.45, 0.7, "a B pillar");
    }

    /** A large SUV's. */
    public BodyFigures() {
        this(100, 106, 172, 196, 0.2, 0.58);
    }

    private void refused(final double value, final double least, final double most, final String what) {
        if (value < least || value > most) {
            throw new IllegalArgumentException(what + " of " + value + " is out of the platform's range, "
                    + least + " to " + most);
        }
    }
}

package melons.innocent.doors.swing;

/**
 * How big a door's leaf is, in centimetres: how wide, how high, and how thick from one face to the other.
 *
 * @param wide  how wide the leaf is, from its hinges to its free edge
 * @param high  how high the leaf is
 * @param thick how thick the leaf is
 */
public record Size(double wide, double high, double thick) {

    public Size {
        if (!(wide >= 50 && wide <= 200) || !(high >= 150 && high <= 400) || !(thick >= 0.8 && thick <= 12)) {
            throw new IllegalArgumentException("a leaf someone walks through: " + wide + " by " + high + " by "
                    + thick);
        }
    }
}

package melons.innocent.glm.model;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The retro saloon's front end, as the maker's square-on side photograph shows it, scaled by its wheelbase and its
 * heights to the drawing's, in millimetres along from the front axle and up from the ground, then made larger as the
 * car's lines are: each front wing from the front door's shut line, over the foot of the windscreen's pillar, forward
 * along its top, the bonnet's shut line, falling gently from the cowl to the nose and rolling down to the headlamp; down
 * its front edge beside the headlamp to the bumper's top; back under the bumper to the wheel's arch, round the arch's
 * lip, and along the sill's foot.
 *
 * @param lines the car's lines
 */
record SantanaFront(SantanaLines lines) {

    /** The front wheel arch's middle, up, and how far round its lip is; the wing's foot. */
    private static final double ARCH_UP = 309;
    private static final double ARCH = 385;
    private static final double FOOT = 292;
    /** The wing's back edge, just ahead of the front door's shut line; where it meets the bumper ahead of the arch. */
    private static final double BACK = -447;
    private static final double UNDER = 500;
    /** Its top, the bonnet's shut line, and its front edge, from the cowl to the bumper: along and up. */
    private static final double[][] TOP = {
        {BACK, 948}, {-207, 916}, {4, 892}, {216, 867}, {428, 842}, {640, 802}, {737, 771}, {778, 748}, {795, 726},
        {801, 690}, {802, 600}, {800, 550}};

    /** A front wing's outline, along and up, in centimetres. */
    List<Spot> wing() {
        final List<Spot> out = new ArrayList<>();
        out.add(at(BACK, FOOT));
        for (final double[] p : TOP) {
            out.add(at(p[0], p[1]));
        }
        final double from = Math.toDegrees(Math.asin((UNDER - ARCH_UP) / ARCH));
        final double to = 180 - Math.toDegrees(Math.asin((FOOT - ARCH_UP) / ARCH));
        final int steps = 24;
        for (int k = 0; k <= steps; k++) {
            final double a = Math.toRadians(from + (to - from) * k / steps);
            out.add(at(ARCH * Math.cos(a), ARCH_UP + ARCH * Math.sin(a)));
        }
        return out;
    }

    /** Where a wing is bolted to the shell: its foot at its back edge, along and up, in centimetres. */
    Spot anchor() {
        return at(BACK, FOOT);
    }

    /** A point of the photograph's, along and up in millimetres, in the platform's centimetres, made larger. */
    private Spot at(final double along, final double up) {
        return new Spot(along * lines.scale() / 10, up * lines.scale() / 10);
    }
}

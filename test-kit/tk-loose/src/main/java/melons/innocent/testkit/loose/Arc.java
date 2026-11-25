package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The way a thing would fly if it were let go: from a spot, going some way, pulled down as everything is, with no
 * air, until it is down at a floor. It is the reckoning the loose body's flight lands exactly on, so what it shows
 * is where the thing goes, as long as nothing is in its way.
 *
 * @param from  where it is let go, in centimetres
 * @param going how it is going as it is let go, in centimetres a second
 * @param floor how high the floor is that it comes down to
 */
record Arc(Spot from, Spot going, double floor) {

    private static final double GRAVITY = 981;
    /** How far apart in time its spots are, in seconds, and how many there are at most. */
    private static final double APART = 0.05;
    private static final int MOST = 100;
    /** How far from where it is let go its first spot is, at the least: nearer ones would be in the thrower's face. */
    private static final double CLEAR = 120;

    /** Where it is each twentieth of a second, from a little way out until it is down. */
    List<Spot> spots() {
        final List<Spot> spots = new ArrayList<>();
        for (int spot = 1; spot <= MOST; spot++) {
            final double after = spot * APART;
            final Spot at = from.plus(going.times(after)).plus(new Spot(0, -GRAVITY * after * after / 2, 0));
            if (at.up() < floor) {
                break;
            }
            if (at.minus(from).length() >= CLEAR) {
                spots.add(at);
            }
        }
        return spots;
    }
}

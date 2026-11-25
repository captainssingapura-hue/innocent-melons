package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;

/**
 * Flight: how a thing goes while nothing touches it, whatever its shape. Gravity pulls its middle down and nothing
 * else acts on it, so its middle follows the one curve every thrown or falling thing follows, and it goes on turning
 * as it was turning. A box, a watermelon and a ball thrown alike fly alike; what they are tells only once they come
 * down. There is no air: nothing slows it, and nothing carries it sideways.
 *
 * <p>Over a tick it is moved by the mean of the velocity it had and the velocity it comes to, which is exactly
 * where gravity's pull takes it: so however long a tick is, it is on the curve, as high and as far as the reckoning
 * says. Where it touches something it is moved by what it has once that has pushed it.
 *
 * @param gravity how fast a falling thing gains speed, in centimetres a second each second
 */
record Flight(double gravity) {

    /** {@code moved} after gravity has pulled for so many {@code seconds}: its velocity changed, its turning not. */
    Moved pulled(final Moved moved, final double seconds) {
        return new Moved(moved.velocity().plus(new Spot(0, -gravity * seconds, 0)), moved.spin());
    }

    /**
     * {@code placed} after so many {@code seconds} in which it went from moving as {@code before} says to moving
     * as {@code after} says: carried by the mean of the two, and turned as it then turns.
     */
    Placed carried(final Placed placed, final Moved before, final Moved after, final double seconds) {
        final Spot spin = after.spin();
        return new Placed(placed.at().plus(before.velocity().plus(after.velocity()).times(seconds / 2)),
                spin.length() == 0 ? placed.turned()
                : placed.turned().then(new Turn(spin, spin.length() * seconds)));
    }
}

package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Reckoning;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Touch;

import java.util.List;
import java.util.Optional;

/**
 * How a loose thing of a shape that rolls is stepped. It keeps its motion from tick to tick, where a thing with
 * corners is stopped dead whenever what it touches changes. Each tick gravity pulls it; then, at each spot it
 * touches, it is pushed just so much that the spot of it there is not moving, neither into what it touches nor
 * along it: it does not slip. What is left of its motion is turning about that spot, which is rolling, and the spot
 * moves round it as it goes. So on a slope a ball gains speed at five sevenths of what a thing sliding freely would,
 * and a thing that falls onto the ground with speed along it rolls on from where it lands.
 *
 * <p>While it touches anything it is slowed by a share of gravity's pull, so on the level it comes to a stop; and
 * stopped, touching, it is counted toward its rest. After it has moved it is set back out of whatever it has run
 * into. A crate or a chock in its way stops it, as the ground does, and is not moved by it.
 *
 * @param reckoning gravity's pull, and how much of it slows what rolls
 * @param slack     how far into something it may be and count as only touching it, in centimetres
 */
record Rolling(Reckoning reckoning, double slack) {

    /** {@code rigid}, standing and moving as {@code now} says, a tick of so many {@code seconds} on. */
    Simulator.Stepped stepped(final Rigid rigid, final Simulator.Mover now, final Simulator.Around around,
                              final double seconds) {
        final List<Touch> touches = around.touches(rigid, now.placed());
        Moved moved = new Moved(now.moved().velocity().plus(new Spot(0, -reckoning.gravity() * seconds, 0)),
                now.moved().spin());
        for (final Touch touch : touches) {
            moved = stuck(rigid, now.placed(), touch, moved);
        }
        if (!touches.isEmpty()) {
            moved = slowed(moved, reckoning.rolling() * reckoning.gravity() * seconds);
        }
        final Spot spin = moved.spin();
        final Placed gone = new Placed(now.placed().at().plus(moved.velocity().times(seconds)),
                spin.length() == 0 ? now.placed().turned()
                        : now.placed().turned().then(new Turn(spin, spin.length() * seconds)));
        Spot out = new Spot(0, 0, 0);
        for (final Touch touch : around.touches(rigid, gone)) {
            if (touch.deep() > slack) {
                out = out.plus(touch.pushes().times(touch.deep()));
            }
        }
        final boolean stopped = !touches.isEmpty() && moved.still();
        return new Simulator.Stepped(new Simulator.Mover(gone.moved(out), moved, stopped,
                stopped ? now.still() + 1 : 0),
                stopped && !now.landed() ? Optional.of(touches.getFirst().on()) : Optional.empty());
    }

    /**
     * {@code moved} after the push at {@code touch} that stops the spot of the thing there: none, where that spot is
     * already leaving what it touches, or where only a pull would hold it.
     */
    private Moved stuck(final Rigid rigid, final Placed placed, final Touch touch, final Moved moved) {
        final Spot arm = touch.at().minus(placed.at());
        final Spot slipping = moved.velocity().plus(moved.spin().cross(arm));
        if (slipping.dot(touch.pushes()) > 1e-9) {
            return moved;
        }
        final double kilograms = rigid.kilograms();
        // what a push each way does to the spot's own velocity: its middle's share, and its turning's
        final Spot across = given(rigid, placed, arm, new Spot(1, 0, 0), kilograms);
        final Spot up = given(rigid, placed, arm, new Spot(0, 1, 0), kilograms);
        final Spot along = given(rigid, placed, arm, new Spot(0, 0, 1), kilograms);
        final double all = across.dot(up.cross(along));
        final Spot wanted = slipping.times(-1);
        final Spot push = new Spot(wanted.dot(up.cross(along)) / all, across.dot(wanted.cross(along)) / all,
                across.dot(up.cross(wanted)) / all);
        if (push.dot(touch.pushes()) < 0) {
            return moved;
        }
        return new Moved(moved.velocity().plus(push.times(1 / kilograms)),
                moved.spin().plus(turned(rigid, placed, arm.cross(push))));
    }

    /** How the velocity of the spot at {@code arm} from the thing's middle changes for a push of one, {@code way}. */
    private Spot given(final Rigid rigid, final Placed placed, final Spot arm, final Spot way,
                       final double kilograms) {
        return way.times(1 / kilograms).minus(arm.cross(turned(rigid, placed, arm.cross(way))));
    }

    /** How the thing's turning changes for a {@code twist} given it: the twist over how hard it is to turn each way. */
    private Spot turned(final Rigid rigid, final Placed placed, final Spot twist) {
        final Spot lying = placed.lying(twist);
        return placed.turned().turned(new Spot(lying.across() / rigid.turnsHard(new Spot(1, 0, 0)),
                lying.up() / rigid.turnsHard(new Spot(0, 1, 0)), lying.along() / rigid.turnsHard(new Spot(0, 0, 1))));
    }

    /** {@code moved} with so much {@code less} speed, and as much less turning with it; stopped, where it had less. */
    private Moved slowed(final Moved moved, final double less) {
        final double speed = moved.velocity().length();
        if (speed <= less || speed == 0) {
            return new Moved();
        }
        return new Moved(moved.velocity().times((speed - less) / speed), moved.spin().times((speed - less) / speed));
    }
}

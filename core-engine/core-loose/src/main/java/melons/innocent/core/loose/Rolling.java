package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Gravity;
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
 * and a thing that falls onto the ground with speed along it rolls on from where it lands. In the air it is in
 * flight as any shape is ({@link Flight}).
 *
 * <p>While it touches anything it is slowed by a share of gravity's pull, so on the level it comes to a stop; and
 * stopped, touching, it is counted toward its rest. After it has moved it is set back out of whatever it has run
 * into. A crate or a chock in its way stops it, as the ground does, and is not moved by it. What it touches may
 * itself be going, a train's deck: then it is pushed, slowed and stopped against that, and so is carried along.
 *
 * @param reckoning gravity's pull, and how much of it slows what rolls
 * @param slack     how far into something it may be and count as only touching it, in centimetres
 */
record Rolling(Reckoning reckoning, double slack, Whys whys, Gravity gravity) {

    /** {@code rigid}, standing and moving as {@code now} says, a tick of so many {@code seconds} on. */
    Out stepped(final Rigid rigid, final Simulator.Mover now, final Simulator.Around around,
                final double seconds) {
        final Flight flight = new Flight(gravity.pull());
        final List<Touch> touches = around.touches(rigid, now.placed());
        Moved moved = flight.pulled(now.moved(), seconds);
        boolean pushed = false;
        // how what it presses on is itself going: it is slowed, and stopped, against that and not against the ground
        Spot frame = new Spot(0, 0, 0);
        for (final Touch touch : touches) {
            final Optional<Moved> stuck = stuck(rigid, now.placed(), touch, moved);
            frame = pushed || stuck.isEmpty() ? frame : touch.going();
            pushed = pushed || stuck.isPresent();
            moved = stuck.orElse(moved);
        }
        // slowed only by what it presses on: thrown up off the ground, or in the air, nothing slows it
        if (pushed) {
            moved = slowed(moved, frame, reckoning.rolling() * gravity.pull() * seconds);
        }
        // pushed by nothing it is carried as flight carries it; pushed, by what it has once it has been pushed
        final Placed gone = flight.carried(now.placed(), pushed ? moved : now.moved(), moved, seconds);
        final List<Touch> met = around.touches(rigid, gone);
        Spot out = new Spot(0, 0, 0);
        for (final Touch touch : met) {
            if (touch.deep() > slack) {
                out = out.plus(touch.pushes().times(touch.deep()));
            }
        }
        final boolean stopped = pushed && moved.velocity().minus(frame).length() == 0 && moved.spin().length() == 0;
        // it has landed in the part of a tick it comes out of the air in, whether it stops there or rolls on
        final Moved rolled = moved;
        return new Out(new Simulator.Mover(gone.moved(out), moved, stopped, stopped ? now.still() + 1 : 0),
                touches.isEmpty() && !met.isEmpty() ? Optional.of(met.getFirst().on()) : Optional.empty(),
                whys.of(() -> new Why.Rolled(rolled.velocity().minus(now.moved().velocity()),
                        touches.stream().map(Touch::on).distinct().toList(), stopped)));
    }

    /**
     * The first correction of a tick's rolling: {@code stepped} put right for what the world then found in the
     * thing's path. At each spot it met something it is pushed straight out of it, just so much that the spot of it
     * there is no longer moving into what it met, as that is going: it is not gripped there, and it is not thrown
     * back. So it stops against what stands still and is taken along by what comes at it; and it goes on with
     * whatever of its path is not into what it met ({@link Barred}). Where it was hardly moving when it met it,
     * a melon leaning on someone's shin, it is stopped against it and counted toward its rest.
     */
    Out correctionS1(final Rigid rigid, final Simulator.Mover now, final Out stepped,
                     final List<Collision> collisions, final double seconds) {
        final Barred barred = new Barred(collisions);
        Moved moved = stepped.next().moved();
        boolean pushed = false;
        for (final Collision collision : collisions) {
            final Optional<Moved> kept = kept(rigid, barred.first().there(), collision.touch(), moved);
            pushed = pushed || kept.isPresent();
            moved = kept.orElse(moved);
        }
        // only touching what it met, or leaving it, as a thing thrown leaves the hand: nothing to put right
        if (!pushed) {
            return stepped;
        }
        // hardly moving when it met it, and what it met hardly moving: it is leaning on it, and stopped against
        // it, as rolling is slowed to a stop on the ground; counted toward its rest where it was
        final double hardly = reckoning.rolling() * gravity.pull() * seconds;
        if (stepped.next().moved().velocity().length() <= hardly
                && barred.first().touch().going().length() <= hardly) {
            return new Out(new Simulator.Mover(new Placed(barred.first().there().at(),
                    stepped.next().placed().turned()), new Moved(), true, now.still() + 1), stepped.on(),
                    stepped.why());
        }
        return new Out(new Simulator.Mover(barred.placed(now, stepped.next()), moved,
                stepped.next().landed(), stepped.next().still()), stepped.on(), stepped.why());
    }

    /**
     * {@code moved} after the push straight out of what is met at {@code touch} that keeps the spot of the thing
     * there from moving into it: none, where that spot is not moving into it at all.
     */
    private Optional<Moved> kept(final Rigid rigid, final Placed placed, final Touch touch, final Moved moved) {
        final Spot arm = touch.at().minus(placed.at());
        final double into = moved.velocity().plus(moved.spin().cross(arm)).minus(touch.going()).dot(touch.pushes());
        if (into >= -1e-9) {
            return Optional.empty();
        }
        final double kilograms = rigid.kilograms();
        final Spot push = touch.pushes().times(-into / given(rigid, placed, arm, touch.pushes(), kilograms)
                .dot(touch.pushes()));
        return Optional.of(new Moved(moved.velocity().plus(push.times(1 / kilograms)),
                moved.spin().plus(turned(rigid, placed, arm.cross(push)))));
    }

    /**
     * {@code moved} after the push at {@code touch} that stops the spot of the thing there: none, where that spot is
     * leaving what it touches or is not moving into it at all, so that a wall or a chock it merely touches does not
     * keep it from falling, or from being thrown along it.
     */
    private Optional<Moved> stuck(final Rigid rigid, final Placed placed, final Touch touch, final Moved moved) {
        final Spot arm = touch.at().minus(placed.at());
        // how the spot of it there is moving against what it touches, which may itself be going
        final Spot slipping = moved.velocity().plus(moved.spin().cross(arm)).minus(touch.going());
        // only what it is moving into holds it: what it merely touches, or is leaving, does not grip it
        if (slipping.dot(touch.pushes()) >= -1e-9) {
            return Optional.empty();
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
        if (push.dot(touch.pushes()) <= 1e-9) {
            return Optional.empty();
        }
        return Optional.of(new Moved(moved.velocity().plus(push.times(1 / kilograms)),
                moved.spin().plus(turned(rigid, placed, arm.cross(push)))));
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

    /**
     * {@code moved} with so much {@code less} speed against what it presses on, which is going as {@code frame}
     * says, and as much less turning with it; stopped against that, where it had less.
     */
    private Moved slowed(final Moved moved, final Spot frame, final double less) {
        final Spot against = moved.velocity().minus(frame);
        final double speed = against.length();
        if (speed <= less || speed == 0) {
            return new Moved(frame, new Spot(0, 0, 0));
        }
        return new Moved(frame.plus(against.times((speed - less) / speed)),
                moved.spin().times((speed - less) / speed));
    }
}

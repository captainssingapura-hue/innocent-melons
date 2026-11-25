package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Reckoned;
import melons.innocent.core.rest.Reckoning;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Touch;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The loose body: the simulator of a thing nothing drives. Each tick it makes the check's own reckoning of the
 * thing's forces ({@link Reckoning}), takes the change of velocity or of turning that comes of it, and moves the
 * thing by what it then has, the new velocity first. So it does one of three things in a tick:
 *
 * <ul>
 * <li><b>It flies</b>, touched by nothing, or thrown up off what it stood on: its velocity gains gravity's pull,
 *     it goes on turning as it was, and it moves by both ({@link Flight}), whatever its shape.</li>
 * <li><b>It tips</b>, touched but not balanced: its turning gains what its weight makes about the line it is held
 *     along, and it is carried round that line, which does not slip.</li>
 * <li><b>It is held</b>, balanced on what it touches: it does not move, and the tick is counted toward its rest.</li>
 * </ul>
 *
 * <p>That is a thing with corners. A thing of a shape that rolls keeps its motion and is stepped otherwise
 * ({@link Rolling}).
 *
 * <p>A landing stops: where falling or tipping would carry it into what could hold it, it is set down just touching
 * and loses all its motion, to be reckoned afresh from there at the next tick. Nothing bounces and nothing slides,
 * and it meets only what is at rest or has itself landed: two loose bodies meeting in the air pass through each
 * other for now.
 *
 * @param reckoning the check it steps by
 * @param slack     how far into something it may be and count as only touching it, in centimetres
 */
public record Loose(Reckoning reckoning, double slack) implements Simulator {

    /** How many times a tipping's last tick is halved to find where it lands. */
    private static final int HALVINGS = 24;
    /** How far a thing may go in one part of a tick, in centimetres: less than the thinnest thing it should meet. */
    private static final double STRIDE = 8;

    /** A loose body on the Earth, touching within a hundredth of a centimetre. */
    public Loose() {
        this(new Reckoning(), 0.01);
    }

    /**
     * A tick of a thing. One going fast is taken through the tick in parts, each short enough that it goes no more
     * than a stride, so it cannot pass through a thin thing between one look and the next; it is counted toward its
     * rest once a tick, however many parts the tick was cut in.
     */
    @Override
    public Stepped stepped(final Rigid rigid, final Mover now, final Around around, final double seconds) {
        final int parts = (int) Math.max(1, Math.ceil(now.moved().velocity().length() * seconds / STRIDE));
        if (parts == 1) {
            return once(rigid, now, around, seconds);
        }
        Mover mover = now;
        Optional<String> on = Optional.empty();
        for (int part = 0; part < parts; part++) {
            final Stepped stepped = once(rigid, mover, around, seconds / parts);
            mover = stepped.next();
            on = on.isPresent() ? on : stepped.on();
        }
        return new Stepped(new Mover(mover.placed(), mover.moved(), mover.landed(),
                mover.landed() ? now.still() + 1 : 0), on);
    }

    /** One part of a tick: what rolls is rolled; what has corners flies, tips, or is held. */
    private Stepped once(final Rigid rigid, final Mover now, final Around around, final double seconds) {
        if (rigid.rolls()) {
            return new Rolling(reckoning, slack).stepped(rigid, now, around, seconds);
        }
        final List<Touch> first = around.touches(rigid, now.placed());
        final Spot frame = first.isEmpty() ? new Spot(0, 0, 0) : first.getFirst().going();
        if (frame.length() == 0) {
            return cornered(rigid, now, around, seconds);
        }
        // on something that is itself going: carried on with it, and stepped as it stands and moves against that
        final Stepped within = cornered(rigid, new Mover(now.placed().moved(frame.times(seconds)),
                new Moved(now.moved().velocity().minus(frame), now.moved().spin()), now.landed(), now.still()),
                (shape, placed) -> around.touches(shape, placed).stream()
                        .map(touch -> touch.carried(touch.going().minus(frame))).toList(), seconds);
        return new Stepped(new Mover(within.next().placed(), new Moved(within.next().moved().velocity().plus(frame),
                within.next().moved().spin()), within.next().landed(), within.next().still()), within.on());
    }

    /** A part of a tick of a thing with corners, among things that stand still: it flies, tips, or is held. */
    private Stepped cornered(final Rigid rigid, final Mover now, final Around around, final double seconds) {
        final List<Touch> touches = around.touches(rigid, now.placed());
        // thrown up off what it stood on, it is in flight though it still touches it
        if (!touches.isEmpty() && touches.stream().allMatch(touch ->
                now.moved().velocity().dot(touch.pushes()) > 0)) {
            return fallen(rigid, now, around, seconds);
        }
        final Reckoned reckoned = reckoning.of(rigid, now.placed(), touches, seconds);
        if (reckoned.held()) {
            // held: stopped where it is, and said to have landed if it was not already down
            return new Stepped(new Mover(now.placed(), new Moved(), true, now.still() + 1),
                    now.landed() ? Optional.empty() : Optional.of(touches.getFirst().on()));
        }
        return reckoned.hinge().map(hinge -> tipped(rigid, now, around, seconds, reckoned, hinge))
                .orElseGet(() -> fallen(rigid, now, around, seconds));
    }

    /** In flight ({@link Flight}): gravity's pull taken, moved by what it then has, and set down where it lands. */
    private Stepped fallen(final Rigid rigid, final Mover now, final Around around, final double seconds) {
        final Flight flight = new Flight(reckoning.gravity());
        final Moved flying = flight.pulled(now.moved(), seconds);
        final Placed fell = flight.carried(now.placed(), now.moved(), flying, seconds);
        final Optional<Touch> met = deepest(around.touches(rigid, fell));
        if (met.isEmpty()) {
            return new Stepped(new Mover(fell, flying, false, 0), Optional.empty());
        }
        // lifted straight up until its deepest corner only touches
        final double lift = around.touches(rigid, fell).stream().filter(touch -> touch.deep() > slack)
                .mapToDouble(touch -> touch.deep() / touch.pushes().up()).max().orElse(0);
        // stopped against what it has come down on, which may itself be going
        return new Stepped(new Mover(fell.moved(new Spot(0, lift, 0)),
                new Moved(met.get().going(), new Spot(0, 0, 0)), true, 0), met.map(Touch::on));
    }

    /** Tipping: its turning about its hinge changed, carried round by it, and stopped where it lands. */
    private Stepped tipped(final Rigid rigid, final Mover now, final Around around, final double seconds,
                           final Reckoned reckoned, final Reckoned.Hinge hinge) {
        final double turning = now.moved().spin().dot(hinge.along()) + reckoned.spin().dot(hinge.along());
        final double angle = turning * seconds;
        final Placed round = carried(now.placed(), hinge, angle);
        final Optional<Touch> met = deepest(around.touches(rigid, round));
        if (met.isEmpty()) {
            final Spot spin = hinge.along().times(turning);
            return new Stepped(new Mover(round, new Moved(spin.cross(round.at().minus(hinge.through())), spin),
                    false, 0), Optional.empty());
        }
        double clear = 0;
        double into = angle;
        for (int halving = 0; halving < HALVINGS; halving++) {
            final double middle = (clear + into) / 2;
            if (deepest(around.touches(rigid, carried(now.placed(), hinge, middle))).isEmpty()) {
                clear = middle;
            } else {
                into = middle;
            }
        }
        return new Stepped(new Mover(carried(now.placed(), hinge, clear), new Moved(), true, 0),
                met.map(Touch::on));
    }

    /** {@code placed} carried round {@code hinge} by so many radians. */
    private Placed carried(final Placed placed, final Reckoned.Hinge hinge, final double angle) {
        return angle == 0 ? placed : placed.about(hinge.through(), new Turn(hinge.along(), angle));
    }

    /** The touch that is furthest into what it touches, of those that are in at all. */
    private Optional<Touch> deepest(final List<Touch> touches) {
        return touches.stream().filter(touch -> touch.deep() > slack).max(Comparator.comparingDouble(Touch::deep));
    }
}

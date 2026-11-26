package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Body;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Gravity;
import melons.innocent.core.rest.Landing;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Reckoning;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Touch;
import melons.innocent.core.step.Event;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.step.Said;
import melons.innocent.core.tick.Tick;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The loose body: the engine of a rigid thing nothing drives, in the one shape of an engine. It keeps nothing: it
 * is handed the thing, how it stands and moves, what is round it and the tick, and gives how the thing stands and
 * moves a tick on, with its explanation ({@link Why}) and, where the thing came down on something, that it landed.
 *
 * <p>It does little itself and hands on to an engine under it, chosen by the thing's shape: what rolls to
 * {@link Rolling}, which keeps its motion; what has corners to {@link Cornered}, which flies, tips or is held, and
 * stops dead where it lands. A thing on something that is itself going is carried on with that for the tick and
 * stepped against it. A thing going fast is taken through the tick in parts.
 *
 * <p>Nothing bounces and nothing slides. In its stepping a thing meets only what is at rest or has itself landed;
 * what stands in its path besides is found by the core afterwards and put right in the first correction.
 *
 * @param reckoning  the check it steps by
 * @param slack      how far into something a thing may be and count as only touching it, in centimetres
 * @param explaining whether it explains what it works out: the run's switch, said once
 */
public record Loose(Reckoning reckoning, double slack, Explaining explaining) implements Simulator<Why> {

    /** How far a thing may go in one part of a tick, in centimetres: less than the thinnest thing it should meet. */
    private static final double STRIDE = 8;

    /** A loose body on the Earth, touching within a hundredth of a centimetre, explaining nothing. */
    public Loose() {
        this(new Explaining());
    }

    /** The same, explaining as {@code explaining} says. */
    public Loose(final Explaining explaining) {
        this(new Reckoning(), 0.01, explaining);
    }

    /**
     * A tick of a thing. One going fast is taken through the tick in parts, each short enough that it goes no more
     * than a stride, so it cannot pass through a thin thing between one look and the next; it is counted toward its
     * rest once a tick, however many parts the tick was cut in.
     */
    @Override
    public Explained<Rigid, Body, Why> simulated(final Body before, final Tick tick,
                                                 final List<? extends Event<Rigid>> events) {
        final Mover now = before.mover();
        final double seconds = tick.seconds();
        final int parts = (int) Math.max(1, Math.ceil(now.moved().velocity().length() * seconds / STRIDE));
        if (parts == 1) {
            return given(before, once(before.rigid(), now, before.around(), seconds, before.gravity()));
        }
        Mover mover = now;
        Optional<String> on = Optional.empty();
        final List<Why> each = new ArrayList<>();
        for (int part = 0; part < parts; part++) {
            final Out out = once(before.rigid(), mover, before.around(), seconds / parts, before.gravity());
            mover = out.next();
            on = on.isPresent() ? on : out.on();
            if (explaining.on()) {
                each.add(out.why());
            }
        }
        return given(before, new Out(new Mover(mover.placed(), mover.moved(), mover.landed(),
                mover.landed() ? now.still() + 1 : 0), on, new Whys(explaining).of(() -> new Why.InParts(each))));
    }

    /**
     * What rolls is corrected as rolling corrects it. A thing with corners goes no further into what it met: it
     * keeps what of its speed, and of its path, is not into that, as that is going. Where there was nothing to put
     * right, the stepping is given back as it was.
     */
    @Override
    public Explained<Rigid, Body, Why> correctionS1(final Body before, final Tick tick,
                                                    final Explained<Rigid, Body, Why> stepped,
                                                    final List<Collision> collisions) {
        final Out regular = new Out(stepped.state().mover(), stepped.said().stream()
                .filter(Landing.class::isInstance).map(said -> ((Landing) said).on()).findFirst(), stepped.why());
        final Out corrected = before.rigid().rolls()
                ? new Rolling(reckoning, slack, new Whys(explaining), before.gravity())
                        .correctionS1(before.rigid(), before.mover(), regular, collisions, tick.seconds())
                : cornered(before.mover(), regular, collisions);
        // only touching what it met, or leaving it: nothing to put right
        if (corrected == regular) {
            return stepped;
        }
        return given(before, new Out(corrected.next(), corrected.on(), new Whys(explaining).of(
                () -> new Why.Corrected(regular.why(), collisions.stream().map(Collision::with).distinct().toList(),
                        regular.next().moved().velocity().minus(corrected.next().moved().velocity())))));
    }

    /** The first correction of a thing with corners: {@code regular} itself, where there is nothing to put right. */
    private Out cornered(final Mover now, final Out regular, final List<Collision> collisions) {
        // held on what it stands on, it is not moved by what meets it: nothing slides, and what rolls into it stops
        if (regular.next().landed()) {
            return regular;
        }
        Spot velocity = regular.next().moved().velocity();
        boolean pushed = false;
        for (final Collision collision : collisions) {
            final double into = velocity.minus(collision.touch().going()).dot(collision.touch().pushes());
            pushed = pushed || into < 0;
            velocity = into < 0 ? velocity.minus(collision.touch().pushes().times(into)) : velocity;
        }
        if (!pushed) {
            return regular;
        }
        return new Out(new Mover(new Barred(collisions).placed(now, regular.next()),
                new Moved(velocity, regular.next().moved().spin()),
                regular.next().landed(), regular.next().still()), regular.on(), regular.why());
    }

    /** What the core is given back: the body as {@code out} leaves it, its explanation, and its landing if any. */
    private Explained<Rigid, Body, Why> given(final Body before, final Out out) {
        return new Explained<>(before.moved(out.next()), out.why(),
                out.on().map(on -> List.<Said>of(new Landing(on))).orElse(List.of()));
    }

    /** One part of a tick: handed on to rolling or to what has corners, on what stands still or in a frame. */
    private Out once(final Rigid rigid, final Mover now, final Around around, final double seconds,
                     final Gravity gravity) {
        final Whys whys = new Whys(explaining);
        if (rigid.rolls()) {
            return new Rolling(reckoning, slack, whys, gravity).stepped(rigid, now, around, seconds);
        }
        final Cornered cornered = new Cornered(reckoning, slack, whys, gravity);
        final List<Touch> first = around.touches(rigid, now.placed());
        final Spot frame = first.isEmpty() ? new Spot(0, 0, 0) : first.getFirst().going();
        if (frame.length() == 0) {
            return cornered.stepped(rigid, now, around, seconds);
        }
        // on something that is itself going: carried on with it, and stepped as it stands and moves against that
        final Out within = cornered.stepped(rigid, new Mover(now.placed().moved(frame.times(seconds)),
                new Moved(now.moved().velocity().minus(frame), now.moved().spin()), now.landed(), now.still()),
                (shape, placed) -> around.touches(shape, placed).stream()
                        .map(touch -> touch.carried(touch.going().minus(frame))).toList(), seconds);
        return new Out(new Mover(within.next().placed(), new Moved(within.next().moved().velocity().plus(frame),
                within.next().moved().spin()), within.next().landed(), within.next().still()), within.on(),
                whys.of(() -> new Why.Carried(frame, within.why())));
    }
}

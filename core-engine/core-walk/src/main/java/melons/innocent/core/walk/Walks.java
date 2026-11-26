package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Landing;
import melons.innocent.core.step.Correcting;
import melons.innocent.core.step.Engine;
import melons.innocent.core.step.Event;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.step.Said;
import melons.innocent.core.tick.Tick;

import java.util.List;
import java.util.Optional;

/**
 * The walking engine, in the one shape of an engine. It keeps nothing: it is handed a walker as it is described,
 * how it stands and goes, what is under it and how hard its world pulls; the tick; and what it is asked. It gives
 * how the walker stands and goes a tick on, with why ({@link Stride}) and, where the walker came down on
 * something, that it landed.
 *
 * <p>It does little itself and hands the walker on to an engine under it, picked by what it finds: {@link OnFoot}
 * where the walker stood on something and a surface is still within a step under its feet, {@link InAir} where
 * there is none. Of several things asked at one tick, the last stands.
 *
 * <p>What stands in a walker's path is not its to find: the core finds that after the stepping, as for every
 * thing in motion, and hands it the collisions. Its first correction takes away the going into what it met, as
 * that is going, and keeps the rest of its path, so it slides along a wall and is pushed along by what comes at it.
 *
 * @param explaining whether it explains what it works out: the run's switch, said once
 */
public record Walks(Explaining explaining)
        implements Engine<Walker, Walking, Stride>, Correcting<Walker, Walking, Stride, Collision> {

    /** How much of a push must be along the ground for it to bar a walker, of one. */
    private static final double LEVEL = 0.3;

    /** A walking engine that explains nothing. */
    public Walks() {
        this(new Explaining());
    }

    @Override
    public Explained<Walker, Walking, Stride> simulated(final Walking before, final Tick tick,
                                                        final List<? extends Event<Walker>> events) {
        Wish wish = new Wish();
        for (final Event<Walker> event : events) {
            wish = event instanceof Wish asked ? asked : wish;
        }
        final Standing now = before.standing();
        final Saying saying = new Saying(explaining);
        final double steps = before.walker().steps();
        final Optional<Stood> on = now.afoot()
                ? before.under().under(new Level().at(now.feet(), now.feet().up() + steps), 2 * steps)
                : Optional.empty();
        final Out out = on.isPresent() ? new OnFoot(saying).stepped(before, wish, on.get(), tick.seconds())
                : new InAir(saying).stepped(before, wish, tick.seconds());
        return new Explained<>(before.stood(out.next()), out.why(),
                out.landed().map(landed -> List.<Said>of(new Landing(landed))).orElse(List.of()));
    }

    /**
     * The first correction: the going into each thing met is taken away, against how that thing is going; the
     * walker is put where it first met anything, and goes on from there by what of its path is not into what it
     * met. Only its going along the ground is barred: it is held up by what is under it and by nothing it meets,
     * so a thing that would push it straight up or down is passed over. Only touching what it met, or leaving it,
     * there is nothing to put right.
     */
    @Override
    public Explained<Walker, Walking, Stride> correctionS1(final Walking before, final Tick tick,
                                                           final Explained<Walker, Walking, Stride> stepped,
                                                           final List<Collision> collisions) {
        final Standing next = stepped.state().standing();
        // a walker is held up only by what is under it: what it meets bars its way along the ground, no more
        final List<Collision> level = collisions.stream()
                .filter(collision -> new Level().of(collision.touch().pushes()).length() > LEVEL).toList();
        Spot going = next.going();
        boolean pushed = false;
        Optional<Collision> nearest = Optional.empty();
        for (final Collision collision : level) {
            final Spot pushes = new Level().of(collision.touch().pushes()).unit();
            final double into = going.minus(collision.touch().going()).dot(pushes);
            pushed = pushed || into < 0;
            going = into < 0 ? going.minus(pushes.times(into)) : going;
            nearest = nearest.isEmpty() || collision.share() < nearest.get().share() ? Optional.of(collision)
                    : nearest;
        }
        if (!pushed) {
            return stepped;
        }
        final Collision first = nearest.get();
        final Spot from = before.standing().feet();
        final Spot path = next.feet().minus(from);
        Spot rest = path.times(1 - first.share());
        for (final Collision collision : level) {
            final Spot pushes = new Level().of(collision.touch().pushes()).unit();
            final double into = rest.dot(pushes);
            rest = into < 0 ? rest.minus(pushes.times(into)) : rest;
        }
        final Spot out = first.share() == 0 ? new Level().of(first.touch().pushes())
                .times(Math.max(0, first.touch().deep())) : new Spot(0, 0, 0);
        final Spot lost = next.going().minus(going);
        final Spot kept = going;
        return new Explained<>(before.stood(new Standing(from.plus(path.times(first.share())).plus(rest).plus(out),
                next.yaw(), kept, next.on(), next.stride())), new Saying(explaining).of(() -> new Stride.Barred(
                        stepped.why(), collisions.stream().map(Collision::with).distinct().toList(), lost)),
                stepped.said());
    }
}

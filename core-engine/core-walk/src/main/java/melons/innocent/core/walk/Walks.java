package melons.innocent.core.walk;

import melons.innocent.core.rest.Landing;
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
 * thing in motion.
 *
 * @param explaining whether it explains what it works out: the run's switch, said once
 */
public record Walks(Explaining explaining) implements Engine<Walker, Walking, Stride> {

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
}

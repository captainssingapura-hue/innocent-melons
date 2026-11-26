package melons.innocent.core.rest;

import melons.innocent.core.tick.Tick;

import java.util.List;

/**
 * What happened in one tick of a world, stamped with the tick it happened in: what a journal keeps, a recorder
 * writes and another player is sent.
 *
 * @param tick     the tick that was taken
 * @param happened what happened in it, in the order it happened
 */
public record Stamped(Tick tick, List<Happened> happened) {

    public Stamped {
        happened = List.copyOf(happened);
    }
}

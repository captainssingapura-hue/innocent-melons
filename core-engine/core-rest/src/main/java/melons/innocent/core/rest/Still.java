package melons.innocent.core.rest;

import melons.innocent.core.step.Event;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explanation;
import melons.innocent.core.tick.Tick;

import java.util.List;

/**
 * The simulator that steps nothing: whatever is handed to it stays as it is. For a world in which nothing is ever
 * let go, or one that is only asked what stands where.
 */
public record Still() implements Simulator<Still.Nothing> {

    /** All it has to explain. */
    public enum Nothing implements Explanation<Rigid> {
        NOTHING
    }

    @Override
    public Explained<Rigid, Body, Nothing> simulated(final Body before, final Tick tick,
                                                     final List<? extends Event<Rigid>> events) {
        return new Explained<>(before, Nothing.NOTHING);
    }
}

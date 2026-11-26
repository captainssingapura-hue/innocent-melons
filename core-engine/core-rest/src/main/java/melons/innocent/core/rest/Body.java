package melons.innocent.core.rest;

import melons.innocent.core.step.Situated;

/**
 * A rigid thing as a simulator is handed it and gives it back: the thing as it is described, how it stands and
 * moves, and what is round it, which is all the simulator may ask of the world.
 *
 * @param rigid  the thing, as it is described
 * @param mover  how it stands and moves
 * @param around what is round it, as the world is when it is handed over
 */
public record Body(Rigid rigid, Simulator.Mover mover, Simulator.Around around) implements Situated<Rigid> {

    /** The same thing among the same things, standing and moving as {@code next} says. */
    public Body moved(final Simulator.Mover next) {
        return new Body(rigid, next, around);
    }
}

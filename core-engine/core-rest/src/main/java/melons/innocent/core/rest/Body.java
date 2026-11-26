package melons.innocent.core.rest;

import melons.innocent.core.step.Situated;

/**
 * A rigid thing as a simulator is handed it and gives it back: the thing as it is described, how it stands and
 * moves, what is round it, which is all the simulator may ask of the world, and how hard that world pulls.
 *
 * @param rigid  the thing, as it is described
 * @param mover  how it stands and moves
 * @param around what is round it, as the world is when it is handed over
 * @param gravity how hard the world it is in pulls it down
 */
public record Body(Rigid rigid, Simulator.Mover mover, Simulator.Around around, Gravity gravity)
        implements Situated<Rigid> {

    /** A thing on the Earth. */
    public Body(final Rigid rigid, final Simulator.Mover mover, final Simulator.Around around) {
        this(rigid, mover, around, new Gravity());
    }

    /** The same thing among the same things, standing and moving as {@code next} says. */
    public Body moved(final Simulator.Mover next) {
        return new Body(rigid, next, around, gravity);
    }
}

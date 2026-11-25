package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.physics.existences.Rolls;
import melons.innocent.physics.existences.Unmoving;

/**
 * A thing that keeps its shape, as the check and the simulators know it: its name, by which the world tells it from
 * every other; its shape; and how dense what it is made of is, in kilograms a cubic metre. Its mass, its middle of
 * mass and how hard it is to set turning are worked out from those, never said beside them: it is of one stuff all
 * through, so its middle of mass is the middle of its shape. Whether it rolls, and whether it ever moves at all,
 * are its shape's kind's to say.
 *
 * @param name  what the world calls it
 * @param shape its shape
 * @param dense how many kilograms a cubic metre of it weighs
 */
public record Rigid(String name, ShapeImpl<?> shape, double dense) {

    public Rigid {
        if (name.isBlank()) {
            throw new IllegalArgumentException("a thing is called something");
        }
        if (!(dense > 0)) {
            throw new IllegalArgumentException(name + " is made of something that weighs: " + dense);
        }
    }

    /** Its mass, in kilograms. */
    public double kilograms() {
        return dense * shape.room();
    }

    /**
     * How hard it is to set turning about a line through its middle, the line {@code along} lying as said in the
     * thing's own frame and one long: in kilograms times centimetres squared.
     */
    public double turnsHard(final Spot along) {
        return shape.turnsHard(along, kilograms());
    }

    /** Whether it is of a shape that rolls. */
    public boolean rolls() {
        return shape.kind().answer(Rolls.class).isPresent();
    }

    /** Whether it is of a shape that never moves: always at rest, holding up what stands on it. */
    public boolean unmoving() {
        return shape.kind().answer(Unmoving.class).isPresent();
    }
}

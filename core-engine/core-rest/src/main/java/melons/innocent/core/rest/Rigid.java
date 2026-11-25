package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * A thing that keeps its shape, as the check and the simulators know it: its name, by which the world tells it from
 * every other; its shape; and how dense what it is made of is, in kilograms a cubic metre. Its mass, its middle of
 * mass and how hard it is to set turning are worked out from those, never said beside them: it is of one stuff all
 * through, so its middle of mass is the middle of its shape.
 *
 * @param name  what the world calls it
 * @param box   its shape
 * @param dense how many kilograms a cubic metre of it weighs
 */
public record Rigid(String name, Box box, double dense) {

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
        return dense * box.room();
    }

    /**
     * How hard it is to set turning about a line through its middle, the line {@code along} lying as said in the
     * thing's own frame and one long: in kilograms times centimetres squared.
     */
    public double turnsHard(final Spot along) {
        final double twelfth = kilograms() / 12;
        final double across = twelfth * (box.up() * box.up() + box.along() * box.along());
        final double up = twelfth * (box.across() * box.across() + box.along() * box.along());
        final double lengthways = twelfth * (box.across() * box.across() + box.up() * box.up());
        return across * along.across() * along.across() + up * along.up() * along.up()
                + lengthways * along.along() * along.along();
    }
}

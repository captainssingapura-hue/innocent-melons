package melons.innocent.core.rest;

/**
 * The ground: level, at one height, the root of what stands on what. It is always at rest and holds whatever stands
 * on it; land that is not level is another root, to come.
 *
 * @param level how high it is, in centimetres
 */
public record Ground(double level) {

    /** The ground where height is nothing. */
    public Ground() {
        this(0);
    }

    /** What a touch on the ground is said to be on: no thing's name. */
    public String name() {
        return "the ground";
    }
}

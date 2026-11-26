package melons.innocent.core.rest;

/**
 * How hard a world pulls everything in it down. It is the world's, part of its state, and no engine's: the core
 * hands it to each engine with the thing it is to work out, so the same engine serves on the Earth, on the Moon and
 * on Mars, and two things in one world always fall alike.
 *
 * @param pull how hard it pulls, in centimetres a second each second
 */
public record Gravity(double pull) {

    public Gravity {
        if (pull < 0) {
            throw new IllegalArgumentException("gravity pulls down, or not at all: " + pull);
        }
    }

    /** The Earth's. */
    public Gravity() {
        this(Globe.EARTH);
    }

    /** That of {@code globe}, at its surface. */
    public Gravity(final Globe globe) {
        this(globe.pull());
    }
}

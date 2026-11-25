package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * A throw: so much energy given to a thing all at once, a way round and a way up. The speed a thing leaves with is
 * what that energy gives its mass, so of two things thrown alike the heavier goes the slower; it is given no turning.
 * The way round is as a compass has it, from ahead (along) round through the right (across); the way up from level.
 *
 * @param joules  how much energy it is given
 * @param round   which way round it is thrown, in degrees from ahead toward the right
 * @param upward  how far up from level it is thrown, in degrees
 */
public record Throw(double joules, double round, double upward) {

    public Throw {
        if (!(joules >= 0) || !Double.isFinite(joules)) {
            throw new IllegalArgumentException("thrown with some energy, or none: " + joules);
        }
        if (!(upward >= -90 && upward <= 90)) {
            throw new IllegalArgumentException("thrown from straight down to straight up: " + upward);
        }
    }

    /** How fast {@code rigid} leaves, thrown so, in centimetres a second. */
    public double speed(final Rigid rigid) {
        return 100 * Math.sqrt(2 * joules / rigid.kilograms());
    }

    /** How {@code rigid} is moving as it leaves, thrown so: its speed that way, and no turning. */
    public Moved of(final Rigid rigid) {
        final double speed = speed(rigid);
        final double level = speed * Math.cos(Math.toRadians(upward));
        return new Moved(new Spot(level * Math.sin(Math.toRadians(round)), speed * Math.sin(Math.toRadians(upward)),
                level * Math.cos(Math.toRadians(round))), new Spot(0, 0, 0));
    }
}

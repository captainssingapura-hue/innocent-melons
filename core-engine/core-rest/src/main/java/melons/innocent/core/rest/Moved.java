package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * How a thing is moving: its middle's velocity, in centimetres a second, and its turning, as a line it turns about
 * as long as how fast it turns, in radians a second, the way a screw goes in looking along the line.
 *
 * @param velocity how its middle moves
 * @param spin     how it turns
 */
public record Moved(Spot velocity, Spot spin) {

    /** Not moving at all. */
    public Moved() {
        this(new Spot(0, 0, 0), new Spot(0, 0, 0));
    }

    /** Whether it is not moving at all. */
    public boolean still() {
        return velocity.length() == 0 && spin.length() == 0;
    }
}

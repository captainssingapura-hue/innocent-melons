package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.Optional;

/**
 * What the check comes to for a thing, for one tick: how its velocity changes and how its turning changes, by what
 * is left of its weight once what holds it has given back all it can. Both nothing, it is held, and at rest.
 * Otherwise it falls, its velocity changed and nothing holding it; or it tips, its turning changed about the line
 * it is still held along.
 *
 * @param velocity how its middle's velocity changes over the tick, in centimetres a second
 * @param spin     how its turning changes over the tick: a line as long as the change, in radians a second
 * @param hinge    the line it tips about, where it tips
 */
public record Reckoned(Spot velocity, Spot spin, Optional<Hinge> hinge) {

    /** The line a thing tips about: a spot it goes through, and the way it lies, one long. */
    public record Hinge(Spot through, Spot along) {
    }

    /** Held: nothing changes. */
    public Reckoned() {
        this(new Spot(0, 0, 0), new Spot(0, 0, 0), Optional.empty());
    }

    /** Whether it is held, so that nothing changes. */
    public boolean held() {
        return velocity.length() == 0 && spin.length() == 0;
    }
}

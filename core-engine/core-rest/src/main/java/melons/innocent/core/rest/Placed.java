package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;

import java.util.List;

/**
 * Where a thing is and how it is turned: where its middle is in the world, and its turning from how it was made.
 *
 * @param at     where its middle is
 * @param turned how it is turned
 */
public record Placed(Spot at, Turn turned) {

    /** Standing at {@code at} as it was made, not turned. */
    public Placed(final Spot at) {
        this(at, new Turn());
    }

    /** A {@code box}'s corners in the world, placed so. */
    public List<Spot> corners(final Box box) {
        return box.corners().stream().map(corner -> at.plus(turned.turned(corner))).toList();
    }

    /** A spot of the world, as it lies in the thing's own frame. */
    public Spot within(final Spot world) {
        return turned.back().turned(world.minus(at));
    }

    /** A way of the world, as it lies in the thing's own frame. */
    public Spot lying(final Spot way) {
        return turned.back().turned(way);
    }

    /** Moved by {@code by}, turned as it is. */
    public Placed moved(final Spot by) {
        return new Placed(at.plus(by), turned);
    }

    /** Turned by {@code turning} about a line through {@code through}: its middle carried round with it. */
    public Placed about(final Spot through, final Turn turning) {
        return new Placed(through.plus(turning.turned(at.minus(through))), turned.then(turning));
    }
}

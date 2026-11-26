package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Placed;

/**
 * What a walker is to other things: a box as wide as it is. To what rolls or is thrown at it, and to what is round,
 * which it cannot step onto, it is the whole of itself, from its feet to its head. Against things with corners its
 * own way is barred as a box that begins a step above its feet: whatever is lower than a step is not in a walker's
 * way, since it steps onto it, so what it stands on never meets it and a ledge it can step up does not bar it.
 *
 * @param walker the walker
 */
public record Bulk(Walker walker) {

    /** The whole of it, from its feet to its head: what rolls or is thrown at it meets this, and what is round. */
    public Box whole() {
        return new Box(walker.wide(), walker.tall(), walker.wide());
    }

    /** Where the whole of it is, its feet at {@code feet} and facing {@code yaw} degrees. */
    public Placed stands(final Spot feet, final double yaw) {
        return new Placed(feet.plus(new Spot(0, walker.tall() / 2, 0)),
                new Turn(new Spot(0, 1, 0), Math.toRadians(yaw)));
    }

    /** The box its own way is barred as, against things with corners: from a step above its feet. */
    public Box box() {
        return new Box(walker.wide(), walker.tall() - walker.steps(), walker.wide());
    }

    /** Where that box is, the walker's feet at {@code feet} and facing {@code yaw} degrees. */
    public Placed placed(final Spot feet, final double yaw) {
        return new Placed(feet.plus(new Spot(0, walker.steps() + (walker.tall() - walker.steps()) / 2, 0)),
                new Turn(new Spot(0, 1, 0), Math.toRadians(yaw)));
    }
}

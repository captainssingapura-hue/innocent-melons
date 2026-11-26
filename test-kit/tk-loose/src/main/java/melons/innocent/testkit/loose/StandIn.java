package melons.innocent.testkit.loose;

import com.example.fxsuite.avatar.sim.Block;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.ShapeImpl;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.rest.Slab;

import java.util.List;

/**
 * What stands in for a thing at rest, to the avatar: one of the engine's blocks. A box standing square is itself. A
 * box or a slab that leans is a ramp along its longest way, from its lower end up to its higher, as wide as it is
 * and solid down to the ground under it, so it is walked up and not through. One standing on end is the box round
 * its corners. A round thing is the box round it, where it lies.
 */
public record StandIn() {

    /** The block that stands in for a thing of {@code shape} placed so. */
    public Block of(final ShapeImpl<?> shape, final Placed placed) {
        return switch (shape) {
            case Box box -> boxed(box, placed);
            case Slab slab -> boxed(slab.box(), placed);
            case Ellipsoid round -> new Block(placed.at().across(), placed.at().along(), round.reaches(),
                    round.reaches(), 0, Math.max(0, placed.at().up() - round.reaches()),
                    placed.at().up() + round.reaches());
            case Shell shell -> throw new IllegalArgumentException("a shell is stood on by its own pavement: " + shell);
        };
    }

    private Block boxed(final Box box, final Placed placed) {
        if (Math.abs(placed.turned().w()) > 0.999_999) {
            return new Block(placed.at().across(), placed.at().along(), box.across() / 2, box.along() / 2, 0,
                    placed.at().up() - box.up() / 2, placed.at().up() + box.up() / 2);
        }
        final int longest = box.across() >= box.up() && box.across() >= box.along() ? 0
                : box.up() >= box.along() ? 1 : 2;
        final Spot reach = placed.turned().turned(new Spot(longest == 0 ? 1 : 0, longest == 1 ? 1 : 0,
                longest == 2 ? 1 : 0)).times(reach(box, longest) / 2);
        final Spot upper = reach.up() >= 0 ? reach : reach.times(-1);
        final List<Spot> corners = placed.corners(box);
        final double high = corners.stream().filter(corner -> corner.minus(placed.at()).dot(upper) > 0)
                .mapToDouble(Spot::up).max().orElseThrow();
        final double low = corners.stream().filter(corner -> corner.minus(placed.at()).dot(upper) < 0)
                .mapToDouble(Spot::up).max().orElseThrow();
        if (Math.hypot(upper.across(), upper.along()) < 1) {
            final double across = corners.stream().mapToDouble(corner -> Math.abs(corner.across()
                    - placed.at().across())).max().orElseThrow();
            final double along = corners.stream().mapToDouble(corner -> Math.abs(corner.along()
                    - placed.at().along())).max().orElseThrow();
            return new Block(placed.at().across(), placed.at().along(), across, along, 0, 0, Math.max(high, low));
        }
        final Spot foot = placed.at().minus(upper);
        final Spot head = placed.at().plus(upper);
        return Block.ramp(foot.across(), foot.along(), Math.max(0, low), head.across(), head.along(), high,
                Math.max(reach(box, (longest + 1) % 3), reach(box, (longest + 2) % 3)));
    }

    /** How far a box reaches the way numbered: 0 across, 1 up, 2 along. */
    private double reach(final Box box, final int way) {
        return way == 0 ? box.across() : way == 1 ? box.up() : box.along();
    }
}

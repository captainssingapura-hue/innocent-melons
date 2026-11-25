package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * A shell's sides and its underside, in the game engine's way: the engine's pavement, which the shell's top is
 * stood on by, is in nobody's way, its edges being taken as buried, so without these a body on the ground walks
 * into a deck's side and stands inside it. A thin wall stands along each edge of the shell, from its underside up
 * to its top there, so it is run into where the edge is higher than a step and lower than the head, and stepped
 * onto where it is no higher than a step. Under the shell hang thin ribs, a lattice of them a metre apart both
 * ways, each a hand deep from the underside down, so a body under the shell is stopped where the underside comes
 * down to its head. None of them reaches over the shell's top, so whoever is on it meets none.
 *
 * <p>Each is one of the engine's own prisms, cut short enough to follow the curve; the engine finds them through
 * its grid, however many there are.
 *
 * @param shell  the shell
 * @param placed where it stands in the world
 */
record Sided(Shell shell, Placed placed) {

    /** How long a piece of wall is at most, and of rib; and how far apart the ribs are: in centimetres. */
    private static final double WALL = 25;
    private static final double RIB = 50;
    private static final double APART = 100;
    /** Half how thick a wall or a rib is; and how deep a rib hangs. */
    private static final double THIN = 2;
    private static final double DEEP = 10;
    /** How many pieces a way over the sheet is measured in. */
    private static final int MEASURED = 64;

    /** Every wall and rib of it. */
    List<Volume> walls() {
        final List<Volume> walls = new ArrayList<>();
        final double far = measured(true);
        final double wide = measured(false);
        for (final double edge : new double[] {0, 1}) {
            run(walls, true, edge, (int) Math.ceil(far / WALL), false);
            if (!closed()) {
                run(walls, false, edge, (int) Math.ceil(wide / WALL), false);
            }
        }
        final int across = (int) Math.ceil(far / APART);
        for (int rib = 1; rib < across; rib++) {
            run(walls, false, (double) rib / across, (int) Math.ceil(wide / RIB), true);
        }
        final int along = (int) Math.ceil(wide / APART);
        for (int rib = 1; rib < along; rib++) {
            run(walls, true, (double) rib / along, (int) Math.ceil(far / RIB), true);
        }
        return walls;
    }

    /**
     * A run of pieces over the sheet: along it at {@code kept} of the way across, or across it at {@code kept} of
     * the way along, in so many {@code pieces}; walls, or ribs.
     */
    private void run(final List<Volume> walls, final boolean along, final double kept, final int pieces,
                     final boolean rib) {
        for (int piece = 0; piece < pieces; piece++) {
            final double from = (double) piece / pieces;
            final double to = (piece + 1.0) / pieces;
            piece(walls, along ? shell.sheet().at(from, kept) : shell.sheet().at(kept, from),
                    along ? shell.sheet().at(to, kept) : shell.sheet().at(kept, to), rib);
        }
    }

    /**
     * One piece between two spots of the sheet's top: a wall from the lower of its undersides up to the lower of
     * its tops, or a rib from under the lower of its undersides up to it. Nothing, where the two are one spot seen
     * from above, as at a dome's middle, or where there is no height to it.
     */
    private void piece(final List<Volume> walls, final Spot one, final Spot other, final boolean rib) {
        final Spot from = placed.at().plus(placed.turned().turned(one));
        final Spot to = placed.at().plus(placed.turned().turned(other));
        final double length = Math.hypot(to.across() - from.across(), to.along() - from.along());
        final double top = Math.min(from.up(), to.up());
        final double under = top - shell.thick();
        if (length < 1) {
            return;
        }
        final double sideX = -(to.along() - from.along()) / length * THIN;
        final double sideZ = (to.across() - from.across()) / length * THIN;
        walls.add(new Prism(List.of(new Vec2(from.across() + sideX, from.along() + sideZ),
                new Vec2(to.across() + sideX, to.along() + sideZ), new Vec2(to.across() - sideX, to.along() - sideZ),
                new Vec2(from.across() - sideX, from.along() - sideZ)), rib ? under - DEEP : under,
                rib ? under : top));
    }

    /** Whether it comes back to where it began along itself, so that it has no ends that way: a ring, a dome. */
    private boolean closed() {
        return shell.sheet().at(0, 0.25).minus(shell.sheet().at(1, 0.25)).length() < 1
                && shell.sheet().at(0, 0.75).minus(shell.sheet().at(1, 0.75)).length() < 1;
    }

    /** How far it is over the sheet, down its middle: along it, or across it. */
    private double measured(final boolean along) {
        double far = 0;
        for (int piece = 0; piece < MEASURED; piece++) {
            final double from = (double) piece / MEASURED;
            final double to = (piece + 1.0) / MEASURED;
            far += (along ? shell.sheet().at(to, 0.5).minus(shell.sheet().at(from, 0.5))
                    : shell.sheet().at(0.5, to).minus(shell.sheet().at(0.5, from))).length();
        }
        return far;
    }
}

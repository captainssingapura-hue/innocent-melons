package melons.innocent.plants.walk;

import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * A road that goes on without end the way the world's third number grows, straying sideways as it goes
 * ({@link Wandering}), made as it is travelled and forgotten behind: like a life, it can be gone back along only a
 * little. It is a point for each step of its wandering and a smooth curve through them, a monotone cubic, which
 * meets each point, turns nowhere sharply and never swings out past the points it runs between.
 *
 * <p>It begins straight, some way behind where the travelling begins, so there is road behind the start. Told how
 * far along it has been reached, it makes itself on ahead of that and lets go of what is far behind. What it says
 * of a place does not change while it has the place: the curve between two points goes by the two either side of
 * them as well, so it answers only where it has those, a point in from each end of what it holds.
 */
public final class Road {

    /** How far ahead of where it has been reached it is made, and how far behind that it is kept, in centimetres. */
    private static final double AHEAD = 44_000;
    private static final double BEHIND = 30_000;

    private final Wandering wandering;
    private final List<Knot> points = new ArrayList<>();
    private Monotone curve;

    /** A road wandering as {@code wandering} does, straight until where nothing is along it. */
    public Road(final Wandering wandering) {
        this.wandering = wandering;
        for (double along = -BEHIND - 2 * Wandering.STEP; along <= 0; along += Wandering.STEP) {
            points.add(new Knot(along, 0));
        }
        reached(0);
    }

    /**
     * Told it has been reached as far along as {@code along}: made on ahead of that, and what is far behind it let
     * go. Whether anything of it changed.
     */
    public boolean reached(final double along) {
        boolean changed = curve == null;
        while (points.getLast().x() < along + AHEAD + Wandering.STEP) {
            final Knot last = points.getLast();
            points.add(new Knot(last.x() + Wandering.STEP, last.y() + wandering.stepped()));
            changed = true;
        }
        while (points.get(1).x() < along - BEHIND - Wandering.STEP) {
            points.removeFirst();
            changed = true;
        }
        if (changed) {
            curve = new Monotone(new Knots(points));
        }
        return changed;
    }

    /** From how far along it answers. */
    public double from() {
        return points.get(1).x();
    }

    /** To how far along it answers. */
    public double to() {
        return points.get(points.size() - 2).x();
    }

    /** Whether it answers so far along. */
    public boolean has(final double along) {
        return along >= from() && along <= to();
    }

    /** How far across its middle is, so far along. */
    public double across(final double along) {
        return curve.at(within(along));
    }

    /** How steeply it goes sideways there: so much across for each centimetre along. */
    public double slope(final double along) {
        return curve.slope(within(along));
    }

    /**
     * How far from its middle line the spot {@code across}, {@code along} is, about: measured across and turned
     * to the road's own way there, which is near enough for a spot beside it.
     */
    public double off(final double across, final double along) {
        final double slope = slope(along);
        return Math.abs(across - across(along)) / Math.sqrt(1 + slope * slope);
    }

    private double within(final double along) {
        if (!has(along)) {
            throw new IllegalArgumentException("the road is had from " + from() + " to " + to() + " along, not at "
                    + along);
        }
        return along;
    }
}

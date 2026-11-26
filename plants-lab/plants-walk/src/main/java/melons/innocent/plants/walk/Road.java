package melons.innocent.plants.walk;

import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * A road that goes on without end both ways, straying sideways as it goes ({@link Wandering}), made as it is
 * travelled: on ahead as the traveller goes on, and on back the other way, straying by chances of its own, as the
 * traveller goes back past where it began. What is made is kept, so it is the same road when it is come back to. It
 * is a point for each step of its wandering and a smooth curve through them, a monotone cubic, which meets each
 * point, turns nowhere sharply and never swings out past the points it runs between.
 *
 * <p>It begins straight, some way behind where the travelling begins, so there is road behind the start. Told how
 * far along it has been reached, it has itself to hand as far each way of that as a forest is ever drawn, and no
 * further: what it says of a place does not change while it has the place, and the curve between two points goes
 * by the two either side of them as well, so it answers only where it has those, a point in from each end of what
 * it has to hand.
 */
public final class Road {

    /** How far each way of where it has been reached it is to hand, which is as far as a forest is ever drawn. */
    private static final double REACH = 124_000;
    /** How long the straight it begins with is, behind where the travelling begins. */
    private static final double STRAIGHT = 30_000;

    private final Wandering ahead;
    private final Wandering back;
    /** Every point made, in order along it. */
    private final List<Knot> made = new ArrayList<>();
    private List<Knot> points = List.of();
    private Monotone curve;

    /** A road wandering as {@code wandering} does, straight until where nothing is along it. */
    public Road(final Wandering wandering) {
        this.ahead = wandering;
        this.back = wandering.other();
        for (double along = -STRAIGHT - 2 * Wandering.STEP; along <= 0; along += Wandering.STEP) {
            made.add(new Knot(along, 0));
        }
        reached(0);
    }

    /**
     * Told it has been reached as far along as {@code along}: made on either way as far as it is to be had from
     * there, and had to hand so far each way and no further. Whether what it has to hand changed.
     */
    public boolean reached(final double along) {
        while (made.getLast().x() < along + REACH + 2 * Wandering.STEP) {
            final Knot last = made.getLast();
            made.add(new Knot(last.x() + Wandering.STEP, last.y() + ahead.stepped()));
        }
        while (made.getFirst().x() > along - REACH - 2 * Wandering.STEP) {
            final Knot first = made.getFirst();
            made.addFirst(new Knot(first.x() - Wandering.STEP, first.y() + back.stepped()));
        }
        final double start = made.getFirst().x();
        final int from = (int) Math.floor((along - REACH - start) / Wandering.STEP) - 1;
        final int to = (int) Math.ceil((along + REACH - start) / Wandering.STEP) + 1;
        if (!points.isEmpty() && points.getFirst().equals(made.get(from)) && points.getLast().equals(made.get(to))) {
            return false;
        }
        points = List.copyOf(made.subList(from, to + 1));
        curve = new Monotone(new Knots(points));
        return true;
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

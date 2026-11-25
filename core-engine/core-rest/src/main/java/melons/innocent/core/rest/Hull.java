package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The ground a thing is held over: the outline, seen from above, round every spot where it is touched from below.
 * A thing whose middle of mass is over it is balanced; one whose middle is outside it tips over the stretch of its
 * edge nearest that middle. Heights do not come into the outline, though each of its corners keeps its own.
 */
public final class Hull {

    /** A stretch of the outline's edge, from one touch to the next; one spot twice, where it is a corner alone. */
    public record Edge(Spot from, Spot to) {

        /** Whether it is a corner alone and not a stretch. */
        public boolean corner() {
            return from.minus(to).length() == 0;
        }
    }

    private final List<Spot> round;

    /** The outline round {@code touched}, of which there is at least one. */
    public Hull(final List<Spot> touched) {
        if (touched.isEmpty()) {
            throw new IllegalArgumentException("an outline round at least one spot");
        }
        final List<Spot> sorted = touched.stream()
                .sorted(Comparator.comparingDouble(Spot::across).thenComparingDouble(Spot::along)).toList();
        final List<Spot> lower = half(sorted);
        final List<Spot> upper = half(sorted.reversed());
        final List<Spot> all = new ArrayList<>(lower.subList(0, Math.max(1, lower.size() - 1)));
        if (upper.size() > 2 || lower.size() > 1) {
            all.addAll(upper.subList(0, Math.max(0, upper.size() - 1)));
        }
        this.round = lower.size() == 1 ? List.of(lower.getFirst()) : List.copyOf(all);
    }

    /** Half of the outline: {@code sorted} gone along once, keeping only the spots that turn it the one way. */
    private List<Spot> half(final List<Spot> sorted) {
        final List<Spot> kept = new ArrayList<>();
        for (final Spot spot : sorted) {
            if (!kept.isEmpty() && flat(kept.getLast(), spot) < 1e-9) {
                continue;
            }
            while (kept.size() >= 2 && turn(kept.get(kept.size() - 2), kept.getLast(), spot) <= 0) {
                kept.removeLast();
            }
            kept.add(spot);
        }
        return kept;
    }

    /** Its corners, going round. */
    public List<Spot> round() {
        return round;
    }

    /** Whether {@code over} is over it, or within {@code slack} centimetres of being so. */
    public boolean holds(final Spot over, final double slack) {
        if (round.size() < 3) {
            return away(nearest(over), over) <= slack;
        }
        for (int corner = 0; corner < round.size(); corner++) {
            final Spot from = round.get(corner);
            final Spot to = round.get((corner + 1) % round.size());
            if (turn(from, to, over) < -slack * flat(from, to)) {
                return false;
            }
        }
        return true;
    }

    /** The stretch of its edge nearest {@code over}: a corner alone, where a corner is nearer than any stretch. */
    public Edge nearest(final Spot over) {
        Edge best = new Edge(round.getFirst(), round.getFirst());
        double least = flat(round.getFirst(), over);
        for (int corner = 0; corner < round.size() && round.size() > 1; corner++) {
            final Spot from = round.get(corner);
            final Spot to = round.get((corner + 1) % round.size());
            final double share = ((over.across() - from.across()) * (to.across() - from.across())
                    + (over.along() - from.along()) * (to.along() - from.along())) / (flat(from, to) * flat(from, to));
            final Edge edge = share <= 0 ? new Edge(from, from) : share >= 1 ? new Edge(to, to) : new Edge(from, to);
            final double away = away(edge, over);
            if (away < least - 1e-9) {
                least = away;
                best = edge;
            }
        }
        return best;
    }

    /** How far {@code over} is from a stretch of edge, seen from above. */
    private double away(final Edge edge, final Spot over) {
        if (edge.corner() || flat(edge.from(), edge.to()) == 0) {
            return flat(edge.from(), over);
        }
        return Math.abs(turn(edge.from(), edge.to(), over)) / flat(edge.from(), edge.to());
    }

    /** How far apart two spots are, seen from above. */
    private double flat(final Spot one, final Spot other) {
        return Math.hypot(one.across() - other.across(), one.along() - other.along());
    }

    /** Which way going from {@code from} to {@code to} and on to {@code on} turns, seen from above, and how much. */
    private double turn(final Spot from, final Spot to, final Spot on) {
        return (to.across() - from.across()) * (on.along() - from.along())
                - (to.along() - from.along()) * (on.across() - from.across());
    }
}

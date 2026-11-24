package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A spread's rings ({@link MuscleForm.Spread}), from across its origin to across its insertion: each fibre from a share
 * along its origin's line to the same share along its insertion's, straight, or bent to pass over the same share along
 * its line over; each ring the fibres' points as far from their origin, so a ring follows its lines where they bend, as
 * a line along the occiput then down the neck does. Each faces square to the sheet where it lies, the first out the
 * way its origin faces, each after the same side as the one before; none further from the next than a sheet's rings
 * are, and none turned further from the next than {@link #TURNING}, so a sheet that turns on itself fast is not
 * folded through itself where it does.
 */
public record SpreadRings() implements StatelessFunctionalObject {

    public static final SpreadRings INSTANCE = new SpreadRings();

    /** How far a spread's rings turn from one to the next at most, degrees, where they are as wide as {@link #WIDE}. */
    private static final double TURNING = 8;
    private static final double WIDE = 2;
    /** How many points each ring's line across is found at. */
    private static final int ACROSS = 16;

    /** {@code m}'s lines posed on the body {@code rig} holds, on the right or the left; a strap's, none. */
    public SpreadLines lines(final Muscle m, final Rig rig, final boolean left) {
        if (!(m.form() instanceof MuscleForm.Spread s)) {
            return new SpreadLines(List.of(), List.of(), List.of(), V3.Z);
        }
        return new SpreadLines(posed(s.origin(), rig, left), posed(s.insertion(), rig, left),
                posed(s.over(), rig, left),
                s.origin().getFirst().way(s.facing(), rig, left).unit());
    }

    /**
     * Its rings along {@code l}, as deep as drawn, {@code origin} times at its origin, {@code insertion} times at its
     * insertion, {@code belly} times midway, rounding between; none further apart than {@code apart}.
     */
    public List<MuscleRings.Ring> rings(final SpreadLines l, final double drawn, final double origin,
                                        final double insertion, final double belly, final double apart) {
        final int n = count(l, apart);
        final List<MuscleRings.Ring> rings = new ArrayList<>();
        V3 before = l.facing();
        for (int i = 0; i < n; i++) {
            final double t = (double) i / (n - 1);
            final double end = origin + (insertion - origin) * t;
            final double deep = drawn * (end + (belly - end) * Math.sin(Math.PI * t));
            final List<V3> path = across(l, t);
            final V3 on = l.along(path, 0.5);
            final V3 span = path.getLast().minus(path.getFirst());
            final double h = 0.5 / n;
            final V3 along = fibre(l, 0.5, Math.min(1, t + h)).minus(fibre(l, 0.5, Math.max(0, t - h))).unit();
            final V3 square = span.cross(along);
            V3 way = square.length() < 1e-9 ? before : square.unit();
            if (way.dot(before) < 0) {
                way = way.times(-1);
            }
            before = way;
            rings.add(new MuscleRings.Ring(on, on.plus(way.times(deep / 2)), way, Optional.of(span), length(path),
                    deep, path));
        }
        return rings;
    }

    /**
     * How many rings along {@code l}: none further apart than {@code apart}, nor, where they are {@link #WIDE} across
     * or more, turned more than {@link #TURNING} from the next; a ring narrower, as one gathering to a point is, turns
     * as it likes, folding nothing.
     */
    public int count(final SpreadLines l, final double apart) {
        final int steps = 64;
        double length = 0;
        double fastest = 0;
        V3 last = fibre(l, 0.5, 0);
        V3 span = fibre(l, 1, 0).minus(fibre(l, 0, 0)).unit();
        for (int k = 1; k <= steps; k++) {
            final double t = (double) k / steps;
            final V3 at = fibre(l, 0.5, t);
            final V3 across = fibre(l, 1, t).minus(fibre(l, 0, t));
            final V3 next = across.unit();
            length += at.minus(last).length();
            if (across.length() >= WIDE) {
                fastest = Math.max(fastest,
                        Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, span.dot(next))))) * steps);
            }
            last = at;
            span = next;
        }
        return Math.max(2, Math.max((int) Math.ceil(length / apart), (int) Math.ceil(fastest / TURNING)) + 1);
    }

    /** The fibres' points {@code t} of the way from their origin, from the lines' first ends to their last. */
    private List<V3> across(final SpreadLines l, final double t) {
        final List<V3> path = new ArrayList<>();
        for (int j = 0; j < ACROSS; j++) {
            path.add(fibre(l, (double) j / (ACROSS - 1), t));
        }
        return path;
    }

    /**
     * Where the fibre from {@code u} along the lines is {@code t} of the way from its origin to its insertion: on the
     * straight line between; or, if it passes over a line, on the curve that leaves the one and meets the other as the
     * straight line does, through the point over halfway.
     */
    private V3 fibre(final SpreadLines l, final double u, final double t) {
        final V3 from = l.along(l.origin(), u);
        final V3 to = l.along(l.insertion(), u);
        if (l.over().isEmpty()) {
            return from.times(1 - t).plus(to.times(t));
        }
        final V3 control = l.along(l.over(), u).times(2).minus(from.plus(to).times(0.5));
        return from.times((1 - t) * (1 - t)).plus(control.times(2 * t * (1 - t))).plus(to.times(t * t));
    }

    private double length(final List<V3> path) {
        double length = 0;
        for (int i = 1; i < path.size(); i++) {
            length += path.get(i).minus(path.get(i - 1)).length();
        }
        return length;
    }

    private List<V3> posed(final List<Anchor> line, final Rig rig, final boolean left) {
        return line.stream().map(a -> a.in(rig, left)).toList();
    }
}

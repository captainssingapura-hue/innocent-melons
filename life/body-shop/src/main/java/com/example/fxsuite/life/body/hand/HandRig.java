package com.example.fxsuite.life.body.hand;

import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;

import java.util.EnumMap;
import java.util.Map;

/**
 * A hand held as a pose has it ({@link HandPose}): where each of its segments is, each turned on the one before it
 * about its joint as the hand hangs at rest, the palm still. Each segment's bend about the line square to the way it
 * runs and out of the palm, so it bends toward the palm; its spread, or the thumb's swing, about the line square to
 * that and to the way it runs. In the hand's frame as on the right side, the papers' centimetres.
 */
public final class HandRig {

    /** A segment's move from as the hand hangs at rest: turned, then shifted. */
    private record Moved(Rot turn, V3 shift) {

        V3 apply(final V3 p) {
            return turn.apply(p).plus(shift);
        }
    }

    private final HandPose pose;
    private final Map<Segment, Moved> moved = new EnumMap<>(Segment.class);

    /** The hand held as {@code pose} has it. */
    public HandRig(final HandPose pose) {
        this.pose = pose;
        for (final Segment s : Segment.values()) {
            final V3 way = s.way();
            final V3 bend = way.cross(V3.Z).unit();
            final V3 across = bend.cross(way).unit();
            Rot own = Rot.NONE;
            for (final FingerDof d : s.dofs()) {
                final double by = pose.get(s, d) - s.rest(d);
                own = own.then(Rot.about(d.bends() ? bend : across, by));
            }
            final V3 j = s.joint();
            final Moved local = new Moved(own, j.minus(own.apply(j)));
            final Moved up = s.parent() == null ? new Moved(Rot.NONE, V3.ZERO) : moved.get(s.parent());
            moved.put(s, new Moved(up.turn().then(local.turn()), up.apply(local.shift())));
        }
    }

    /** How it is held. */
    public HandPose pose() {
        return pose;
    }

    /** {@code p}, a point fixed to {@code segment} as the hand hangs at rest, where it is as the hand is held. */
    public V3 place(final Segment segment, final V3 p) {
        return moved.get(segment).apply(p);
    }

    /** {@code segment}'s move from rest as a turn, its nine numbers row by row, and a shift: for what draws it. */
    public double[] move(final Segment segment) {
        final Moved m = moved.get(segment);
        final Rot r = m.turn();
        return new double[] {r.xx(), r.xy(), r.xz(), m.shift().x(), r.yx(), r.yy(), r.yz(), m.shift().y(), r.zx(),
            r.zy(), r.zz(), m.shift().z()};
    }

    /**
     * The middle of the ring {@code d}'s knuckle and its next two joints lie on, as the hand is held: closed round a
     * bar, where the bar's middle is.
     */
    public V3 ring(final Digit d) {
        final Segment first = first(d);
        final V3 a = first.joint();
        final Segment second = next(first);
        final V3 b = place(first, second.joint());
        final V3 c = place(second, next(second).joint());
        final V3 ab = b.minus(a);
        final V3 ac = c.minus(a);
        final V3 n = ab.cross(ac);
        return a.plus(n.cross(ab).times(ac.dot(ac)).plus(ac.cross(n).times(ab.dot(ab))).times(1 / (2 * n.dot(n))));
    }

    /** {@code d}'s first phalanx. */
    private Segment first(final Digit d) {
        for (final Segment s : Segment.values()) {
            if (s.digit() == d && s.phalanx() == 0) {
                return s;
            }
        }
        throw new IllegalArgumentException(d.said() + " has no first phalanx");
    }

    /** The segment turning on {@code s}. */
    private Segment next(final Segment s) {
        for (final Segment n : Segment.values()) {
            if (n.parent() == s) {
                return n;
            }
        }
        throw new IllegalArgumentException(s + " has none on it");
    }
}

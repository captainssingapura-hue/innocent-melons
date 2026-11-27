package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.hand.HandRig;
import com.example.fxsuite.life.body.hand.Segment;
import com.example.fxsuite.vulkan.space.Mat4;

import java.util.EnumMap;
import java.util.Map;

/**
 * How each of a hand's fingers' pieces moves, as the hand rig holds the hand ({@link HandRig}), for what draws them:
 * a piece made on the body as it is bound, taken into its hand's own frame, moved there as its segment is, and put
 * back, all in the body's frame as bound; so that its hand's move, then, carries it on with the hand. The left hand's
 * own frame the right's mirror, put back mirrored, so its pieces keep their faces' way.
 */
public final class HandMoves {

    /** What moves each segment's pieces of {@code hand}, on the body {@code bound} holds, as {@code rig} holds it. */
    public Map<Segment, Mat4> of(final Rig bound, final Part hand, final HandRig rig) {
        final Rig.Frame f = bound.frame(hand);
        final Mat4 out = matrix(f.place(V3.ZERO), f.place(V3.X), f.place(V3.Y), f.place(V3.Z));
        final Mat4 in = matrix(f.local(V3.ZERO), f.local(V3.X), f.local(V3.Y), f.local(V3.Z));
        final Map<Segment, Mat4> moves = new EnumMap<>(Segment.class);
        for (final Segment s : Segment.values()) {
            final double[] m = rig.move(s);
            final Mat4 own = new Mat4(new double[] {m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9],
                m[10], m[11], 0, 0, 0, 1});
            moves.put(s, out.times(own).times(in));
        }
        return moves;
    }

    /** The matrix taking nought to {@code o} and each way to where {@code x}, {@code y} and {@code z} are from it. */
    private Mat4 matrix(final V3 o, final V3 x, final V3 y, final V3 z) {
        final V3 a = x.minus(o);
        final V3 b = y.minus(o);
        final V3 c = z.minus(o);
        return new Mat4(new double[] {a.x(), b.x(), c.x(), o.x(), a.y(), b.y(), c.y(), o.y(), a.z(), b.z(), c.z(),
            o.z(), 0, 0, 0, 1});
    }
}

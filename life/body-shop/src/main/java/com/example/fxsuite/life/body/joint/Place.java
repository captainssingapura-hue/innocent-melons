package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * Where a frame is and how it is turned, in the frame it is given in: its origin {@code at}, and {@code turn}, taking a
 * way given in it to the way it is in the other. A bone's place takes its own frame into the body's; a point's takes
 * the point's frame into its bone's.
 *
 * @param at   where its origin is
 * @param turn how it is turned
 */
public record Place(V3 at, Rot turn) implements Scalar {

    /** Where the frame it is given in is, unturned. */
    public static final Place HERE = new Place(V3.ZERO, Rot.NONE);

    /** A point given in this frame, in the one it is given in. */
    public V3 apply(final V3 p) {
        return at.plus(turn.apply(p));
    }

    /** {@code inner}, a frame given in this one, in the one this is given in. */
    public Place then(final Place inner) {
        return new Place(apply(inner.at()), turn.then(inner.turn()));
    }

    /** This frame's opposite: the frame it is given in, given in it. */
    public Place inverse() {
        final Rot back = turn.back();
        return new Place(back.apply(at).times(-1), back);
    }
}

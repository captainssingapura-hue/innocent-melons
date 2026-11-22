package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.Warp;
import com.example.fxsuite.life.body.Warped;

import java.util.List;

/**
 * The skull and its jaw as drawn bent smoothly ({@link SkullBending}) through their landmarks to where they are to be,
 * each on the left as its fellow on the right; held where they are, the landmarks held and the joint the head nods on,
 * the jaw's condyles, the inner ears' windows. {@code forward} taking a point as drawn to where it is bent to,
 * {@code back} the other way.
 *
 * @param forward from as drawn to as bent
 * @param back    from as bent to as drawn
 */
public record SkullBend(Warp forward, Warp back) {

    /** How much further round a bent shape its box is taken, cm, the bending not quite straight between corners. */
    private static final double ROOM = 1;

    /** Where {@code p}, as drawn in the head's frame, is bent to. */
    public V3 apply(final V3 p) {
        return forward.apply(p);
    }

    /** {@code drawn}, a shape drawn in the head's frame, bent. */
    public Field.Compound shaped(final Field.Compound drawn) {
        final double[] b = drawn.box();
        final double[] box = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int k = 0; k < 8; k++) {
            final V3 corner = new V3(b[(k & 1) == 0 ? 0 : 3], b[(k & 2) == 0 ? 1 : 4], b[(k & 4) == 0 ? 2 : 5]);
            final V3 c = forward.apply(corner);
            final double[] v = {c.x(), c.y(), c.z()};
            for (int i = 0; i < 3; i++) {
                box[i] = Math.min(box[i], v[i] - ROOM);
                box[i + 3] = Math.max(box[i + 3], v[i] + ROOM);
            }
        }
        return Field.Compound.of(List.of(new Field.Element("bent", new Warped(drawn, back, box), Field.Op.ADD, 0)));
    }
}

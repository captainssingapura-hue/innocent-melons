package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.ThinPlate;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.Warp;
import com.example.fxsuite.life.body.Warped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The skull and its jaw as men's are measured: as drawn, bent smoothly ({@link ThinPlate}) through their landmarks to
 * where those are to be ({@link SkullLandmarks}), each landmark on the left as its fellow on the right; held where they
 * are, as are the landmarks held: the joint the head nods on, the jaw's condyles, the inner ears' windows. What is
 * drawn on the head, and what is held on it, bent alike, so that a muscle held on the skull stays on it. Found once.
 */
public enum SkullForm {
    INSTANCE;

    /** How much further round a bent shape its box is taken, cm, the bending not quite straight between corners. */
    private static final double ROOM = 1;

    private final Warp forward;
    private final Warp back;

    SkullForm() {
        final Map<Landmark, V3> to = SkullLandmarks.INSTANCE.targets();
        final List<V3> drawn = new ArrayList<>();
        final List<V3> measured = new ArrayList<>();
        for (final Landmark l : Landmark.values()) {
            drawn.add(l.drawn());
            measured.add(to.get(l));
            if (!l.middle()) {
                drawn.add(mirror(l.drawn()));
                measured.add(mirror(to.get(l)));
            }
        }
        for (final V3 held : List.of(V3.ZERO, SkullContacts.CONDYLE, SkullContacts.OVAL_WINDOW)) {
            drawn.add(held);
            measured.add(held);
            if (held.x() != 0) {
                drawn.add(mirror(held));
                measured.add(mirror(held));
            }
        }
        forward = ThinPlate.INSTANCE.through(drawn, measured);
        back = ThinPlate.INSTANCE.through(measured, drawn);
    }

    /** Where {@code p}, as drawn in the head's frame, is on the skull as measured. */
    public V3 apply(final V3 p) {
        return forward.apply(p);
    }

    /** {@code drawn}, a shape drawn in the head's frame, as the skull is bent. */
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
        return Field.Compound.of(List.of(new Field.Element("as men's are measured", new Warped(drawn, back, box),
                Field.Op.ADD, 0)));
    }

    private V3 mirror(final V3 p) {
        return new V3(-p.x(), p.y(), p.z());
    }
}

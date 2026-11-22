package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.ThinPlate;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.Warp;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The skull bent ({@link SkullBend}) through its landmarks to where they are to be ({@link ThinPlate}), each on the
 * left as its fellow on the right; held where they are, the joint the head nods on, the jaw's condyles, the inner
 * ears' windows.
 */
public record SkullBending() implements StatelessFunctionalObject {

    public static final SkullBending INSTANCE = new SkullBending();

    /** The skull bent through its landmarks to {@code to}, each on the right if one of a pair. */
    public SkullBend through(final Map<Landmark, V3> to) {
        final List<V3> drawn = new ArrayList<>();
        final List<V3> bent = new ArrayList<>();
        for (final Landmark l : Landmark.values()) {
            pair(drawn, bent, l.drawn(), to.get(l), !l.middle());
        }
        for (final V3 held : List.of(V3.ZERO, SkullContacts.CONDYLE, SkullContacts.OVAL_WINDOW)) {
            pair(drawn, bent, held, held, held.x() != 0);
        }
        final Warp forward = ThinPlate.INSTANCE.through(drawn, bent);
        return new SkullBend(forward, ThinPlate.INSTANCE.through(bent, drawn));
    }

    private void pair(final List<V3> drawn, final List<V3> bent, final V3 a, final V3 b, final boolean both) {
        drawn.add(a);
        bent.add(b);
        if (both) {
            drawn.add(new V3(-a.x(), a.y(), a.z()));
            bent.add(new V3(-b.x(), b.y(), b.z()));
        }
    }
}

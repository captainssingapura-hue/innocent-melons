package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * Where a piece of armour lies and what cuts it, each said in its part's own frame as the papers' figure has it
 * ({@link Rig.Frame}), in the body's: the box round it, the planes across its part that bound it, the oval cap round a
 * joint.
 */
public record Bounds() implements StatelessFunctionalObject {

    public static final Bounds INSTANCE = new Bounds();

    /** The box round {@code b}'s corners, a box in {@code f}'s frame, in the body's: lowest x, y, z, then highest. */
    public double[] box(final Rig.Frame f, final double[] b) {
        final double[] out = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int i = 0; i < 8; i++) {
            final V3 p = f.place(new V3(b[(i & 1) * 3], b[1 + (i >> 1 & 1) * 3], b[2 + (i >> 2 & 1) * 3]));
            final double[] v = {p.x(), p.y(), p.z()};
            for (int a = 0; a < 3; a++) {
                out[a] = Math.min(out[a], v[a]);
                out[a + 3] = Math.max(out[a + 3], v[a]);
            }
        }
        return out;
    }

    /** What cuts a piece in {@code f} to between {@code top} and {@code foot} along it. */
    public List<Field.Element> between(final Rig.Frame f, final double top, final double foot) {
        return List.of(cut("top", f, new V3(0, top, 0), V3.Y, 0), cut("foot", f, new V3(0, foot, 0),
                new V3(0, -1, 0), 0));
    }

    /**
     * What cuts away all of a piece in {@code f} beyond the plane through {@code at}, the way {@code out} goes from it,
     * blended {@code blend} into the rest of its edges; called {@code name}.
     */
    public Field.Element cut(final String name, final Rig.Frame f, final V3 at, final V3 out, final double blend) {
        return new Field.Element(name, Field.Plane.of(f.place(at), f.way(out)), Field.Op.CLIP, blend);
    }

    /** An oval about {@code c} in {@code f}, as far as {@code r} says along each of its ways. */
    public Field.Shape cap(final Rig.Frame f, final V3 c, final V3 r) {
        final V3 k = f.scale();
        return Field.Ellipsoid.of(f.place(c), f.way(V3.X).times(r.x() * k.x()), f.way(V3.Y).times(r.y() * k.y()),
                f.way(V3.Z).times(r.z() * k.z()));
    }
}

package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Graft;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A head made as clay is: its blank, and the pieces put on it, each a solid of its own, pressed on and smoothed into it
 * along a seam ({@link Field}). A piece taken from a reference head: its surface made a solid within its feature's
 * region ({@link FeatureMasks}), the side the blank is on its inside ({@link MeshSolid}), cut down to the region, the
 * cut softened; what of it stands out of the blank, its feature, the rest under the blank, lost in it. A feature both
 * standing out of the blank and sunk into it, an ear, grafted in instead ({@link Graft}): within its region the
 * reference's surface in place of the blank's, going over into it toward the region's side.
 */
public record Pieces() implements StatelessFunctionalObject {

    public static final Pieces INSTANCE = new Pieces();

    /** How finely a piece is sampled, cm; how soft its cut, how wide its seam with the blank, cm. */
    private static final double CELL = 0.12;
    private static final double CUT = 0.3;
    private static final double SEAM = 0.5;
    /** How far in from its region's side a graft goes over into the blank, cm. */
    private static final double GRAFT = 1;

    /** Feature {@code f} of the reference {@code r}, as a piece to put on the blank {@code blank}. */
    public Field.Shape of(final ReferenceHead r, final Feature f, final Field.Shape blank) {
        final Field.Ellipsoid region = region(r, f);
        return Field.Compound.of(List.of(new Field.Element("its surface", surface(r, region, blank), Field.Op.ADD, 0),
                new Field.Element("its region", region, Field.Op.CLIP, CUT)));
    }

    /** {@code onto}, a head whose blank is {@code blank}, with feature {@code f} of the reference {@code r} grafted. */
    public Field.Shape grafted(final ReferenceHead r, final Feature f, final Field.Shape blank,
            final Field.Shape onto) {
        final Field.Ellipsoid region = region(r, f);
        return new Graft(onto, surface(r, region, blank), region, GRAFT);
    }

    /** Feature {@code f}'s region on {@code r}. */
    private Field.Ellipsoid region(final ReferenceHead r, final Feature f) {
        final FeatureMasks.Region g = FeatureMasks.INSTANCE.regions(r).get(f);
        return Field.Ellipsoid.of(g.middle(), new V3(g.half().x(), 0, 0), new V3(0, g.half().y(), 0),
                new V3(0, 0, g.half().z()));
    }

    /** The reference's surface made a solid round {@code region}, on the side the blank {@code blank} is inside. */
    private Field.Sampled surface(final ReferenceHead r, final Field.Ellipsoid region, final Field.Shape blank) {
        final double[] b = region.box();
        final double[] box = {b[0] - CUT, b[1] - CUT, b[2] - CUT, b[3] + CUT, b[4] + CUT, b[5] + CUT};
        return MeshSolid.INSTANCE.within(r.mesh(), box, CELL, blank);
    }

    /** The head: {@code blank} with {@code pieces} pressed on it. */
    public Field head(final Field.Shape blank, final List<Field.Shape> pieces) {
        return head(blank, pieces, List.of());
    }

    /** The head: {@code blank} with {@code pieces} pressed on it, and {@code then} laid on it, its eyes, say. */
    public Field head(final Field.Shape blank, final List<Field.Shape> pieces, final List<Field.Element> then) {
        final List<Field.Element> e = new java.util.ArrayList<>();
        e.add(new Field.Element("the blank", blank, Field.Op.ADD, 0));
        for (final Field.Shape p : pieces) {
            e.add(new Field.Element("a piece", p, Field.Op.ADD, SEAM));
        }
        e.addAll(then);
        return new Field(e);
    }
}

package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Graft;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A head made as clay is: its blank, and the pieces put on it, each a solid of its own, pressed on and smoothed into it
 * along a seam ({@link Field}). A piece taken from a reference head: its surface made a solid within its feature's
 * region ({@link FeatureMasks}), the side the blank is on its inside ({@link MeshSolid}), cut down to the region, the
 * cut softened; what of it stands out of the blank, its feature, the rest under the blank, lost in it. A feature both
 * standing out of the blank and sunk into it, an ear, grafted in instead ({@link Graft}): within its region the
 * reference's surface in place of the blank's, going over into it toward the region's side. Each piece adjusted
 * ({@link Adjust}) about its root, where it meets the blank.
 */
public record Pieces() implements StatelessFunctionalObject {

    public static final Pieces INSTANCE = new Pieces();

    /** How finely a piece is sampled, cm; how soft its cut, how wide its seam with the blank, cm. */
    private static final double CELL = 0.12;
    private static final double CUT = 0.3;
    private static final double SEAM = 0.5;
    /** How far in from its region's side a graft goes over into the blank, cm. */
    private static final double GRAFT = 1;
    /** How far a piece's root is looked for, cm, and in steps how long, cm. */
    private static final double REACH = 6;
    private static final double STEP = 0.05;

    /** A feature cut from the reference: its surface made a solid round its region; its root, on the blank. */
    public record Cut(Field.Sampled surface, Field.Ellipsoid region, V3 root) {
    }

    /** What the head is made of from {@code r}, its blank made a solid {@code blank}: each piece cut, once. */
    public Clay clay(final ReferenceHead r, final Field.Shape blank) {
        final Map<Feature, Cut> cuts = new EnumMap<>(Feature.class);
        for (final Feature f : List.of(Feature.NOSE, Feature.MOUTH, Feature.RIGHT_EAR, Feature.LEFT_EAR)) {
            cuts.put(f, cut(r, f, blank));
        }
        final Map<Feature, List<Field.Element>> eyes = new EnumMap<>(Feature.class);
        final Map<Feature, V3> middles = new EnumMap<>(Feature.class);
        for (final Feature f : Piece.EYES.features()) {
            eyes.put(f, Eyes.INSTANCE.of(r, blank, f));
            middles.put(f, Eyes.INSTANCE.eye(r, f).middle());
        }
        return new Clay(blank, cuts, eyes, middles);
    }

    /** Feature {@code f} of the reference {@code r}, cut to be put on the blank {@code blank}. */
    public Cut cut(final ReferenceHead r, final Feature f, final Field.Shape blank) {
        final FeatureMasks.Region g = FeatureMasks.INSTANCE.regions(r).get(f);
        final Field.Ellipsoid region = Field.Ellipsoid.of(g.middle(), new V3(g.half().x(), 0, 0),
                new V3(0, g.half().y(), 0), new V3(0, 0, g.half().z()));
        final double[] b = region.box();
        final double[] box = {b[0] - CUT, b[1] - CUT, b[2] - CUT, b[3] + CUT, b[4] + CUT, b[5] + CUT};
        return new Cut(MeshSolid.INSTANCE.within(r.mesh(), box, CELL, blank), region, root(g.middle(), blank));
    }

    /** Feature {@code f} of the reference {@code r}, as a piece to put on the blank {@code blank}, as it is. */
    public Field.Shape of(final ReferenceHead r, final Feature f, final Field.Shape blank) {
        return piece(cut(r, f, blank));
    }

    /** {@code c} as a piece to put on the blank, adjusted by {@code a} about its root, mirrored if {@code mirrored}. */
    public Field.Shape piece(final Cut c, final Adjust a, final boolean mirrored) {
        return a.on(piece(c), c.root(), mirrored);
    }

    /** {@code onto} with {@code c} grafted in, adjusted by {@code a} about its root, mirrored if {@code mirrored}. */
    public Field.Shape grafted(final Cut c, final Adjust a, final boolean mirrored, final Field.Shape onto) {
        return new Graft(onto, a.on(c.surface(), c.root(), mirrored), a.on(c.region(), c.root(), mirrored), GRAFT);
    }

    /** The head: {@code blank} with {@code pieces} pressed on it, and {@code then} laid on it, its eyes, say. */
    public Field head(final Field.Shape blank, final List<Field.Shape> pieces, final List<Field.Element> then) {
        final List<Field.Element> e = new ArrayList<>();
        e.add(new Field.Element("the blank", blank, Field.Op.ADD, 0));
        for (final Field.Shape p : pieces) {
            e.add(new Field.Element("a piece", p, Field.Op.ADD, SEAM));
        }
        e.addAll(then);
        return new Field(e);
    }

    /** {@code c}'s surface cut down to its region. */
    private Field.Shape piece(final Cut c) {
        return Field.Compound.of(List.of(new Field.Element("its surface", c.surface(), Field.Op.ADD, 0),
                new Field.Element("its region", c.region(), Field.Op.CLIP, CUT)));
    }

    /**
     * Where a piece about {@code middle} meets the blank {@code blank}: the blank's surface on the way from it toward
     * the head's upright middle, or away from it, if it is inside the blank; the middle itself, if none is near.
     */
    private V3 root(final V3 middle, final Field.Shape blank) {
        final V3 axis = new V3(0, middle.y(), 0).minus(middle);
        if (axis.length() < 1e-9) {
            return middle;
        }
        final boolean outside = blank.at(middle.x(), middle.y(), middle.z()) > 0;
        final V3 way = axis.unit().times(outside ? STEP : -STEP);
        V3 p = middle;
        for (double d = 0; d < REACH; d += STEP) {
            p = p.plus(way);
            if (blank.at(p.x(), p.y(), p.z()) > 0 != outside) {
                return p;
            }
        }
        return middle;
    }
}

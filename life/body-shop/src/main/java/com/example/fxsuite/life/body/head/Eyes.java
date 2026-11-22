package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A head's eyes, as a sculptor makes them: each eye's opening carved out of the head, an ellipsoid between its lids,
 * about the middle of its lids' joints, at the front of its eyeball, as wide as its eyeball and part as high as its
 * lids' joints are apart; its eyeball set in it, a ball as the reference's is; its lids put on, the reference's skin
 * round the eye made a piece ({@link Pieces}) whose inside is the head with its opening carved, the opening carved out
 * of it too, so that the lids do not close it again.
 */
public record Eyes() implements StatelessFunctionalObject {

    public static final Eyes INSTANCE = new Eyes();

    /** How deep an opening is carved, each way, cm; how high, each way, as part of the gap between its lids' joints. */
    private static final double DEEP = 1;
    private static final double OPEN = 0.25;
    /** How softly an opening is carved and an eyeball set, cm. */
    private static final double SOFT = 0.15;
    private static final double SET = 0.05;

    /** The eyes' layers, to be laid on a head whose blank is {@code blank}, after its other pieces. */
    public List<Field.Element> of(final ReferenceHead r, final Field.Shape blank) {
        final List<Field.Element> e = new ArrayList<>();
        for (final Feature f : List.of(Feature.RIGHT_EYE, Feature.LEFT_EYE)) {
            final ReferenceHead.Eye eye = f == Feature.RIGHT_EYE ? r.rightEye() : r.leftEye();
            final Field.Ellipsoid open = opening(eye);
            final Field.Shape carved = Field.Compound.of(List.of(new Field.Element("the blank", blank, Field.Op.ADD, 0),
                    new Field.Element("the opening", open, Field.Op.CARVE, SOFT)));
            final double b = eye.radius();
            e.add(new Field.Element(f + "'s opening", open, Field.Op.CARVE, SOFT));
            e.add(new Field.Element(f + "'s ball", Field.Ellipsoid.of(eye.middle(), new V3(b, 0, 0), new V3(0, b, 0),
                    new V3(0, 0, b)), Field.Op.ADD, SET));
            final Field.Shape lids = Field.Compound.of(List.of(new Field.Element("the lids",
                    Pieces.INSTANCE.of(r, f, carved), Field.Op.ADD, 0),
                    new Field.Element("the opening", open, Field.Op.CARVE, SOFT)));
            e.add(new Field.Element(f + "'s lids", lids, Field.Op.ADD, SET));
        }
        return e;
    }

    /** The opening of {@code eye}: between its lids, at the front of its eyeball, {@link #DEEP} each way. */
    private Field.Ellipsoid opening(final ReferenceHead.Eye eye) {
        final V3 lids = eye.upperLid().plus(eye.lowerLid()).times(0.5);
        final double high = OPEN * Math.abs(eye.upperLid().y() - eye.lowerLid().y());
        final V3 mid = new V3(lids.x(), lids.y(), eye.middle().z() + eye.radius());
        return Field.Ellipsoid.of(mid, new V3(eye.radius(), 0, 0), new V3(0, high, 0), new V3(0, 0, DEEP));
    }
}

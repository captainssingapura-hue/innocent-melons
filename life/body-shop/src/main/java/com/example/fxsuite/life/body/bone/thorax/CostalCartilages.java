package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.bone.Kind;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * One side's costal cartilages, drawn in the body's frame at rest and moving with the chest: from each rib's end to the
 * sternum, to the cartilage above, or a free tip. Not bone, so not counted among the bones.
 */
public record CostalCartilages() implements Bone {

    public static final CostalCartilages INSTANCE = new CostalCartilages();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<CostalCartilages> {
        FIRST("the first rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.FIRST)),
        SECOND("the second rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.SECOND)),
        THIRD("the third rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.THIRD)),
        FOURTH("the fourth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.FOURTH)),
        FIFTH("the fifth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.FIFTH)),
        SIXTH("the sixth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.SIXTH)),
        SEVENTH("the seventh rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.SEVENTH)),
        EIGHTH("the eighth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.EIGHTH)),
        NINTH("the ninth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.NINTH)),
        TENTH("the tenth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.TENTH)),
        ELEVENTH("the eleventh rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.ELEVENTH)),
        TWELFTH("the twelfth rib's end", () -> ThoraxContacts.INSTANCE.costochondral(Rib.TWELFTH)),
        STERNUM("where they reach the sternum", () -> ThoraxContacts.INSTANCE.sternocostal());

        private final String said;
        private final Supplier<Place> frame;

        Points(final String said, final Supplier<Place> frame) {
            this.said = said;
            this.frame = frame;
        }

        @Override
        public String said() {
            return said;
        }

        @Override
        public Place frame() {
            return frame.get();
        }
    }

    @Override
    public String name() {
        return "costal cartilages";
    }

    @Override
    public Region region() {
        return Region.THORAX;
    }

    @Override
    public Part part() {
        return Part.CHEST;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public Kind kind() {
        return Kind.CARTILAGE;
    }

    @Override
    public Paint paint() {
        return Paint.CARTILAGE;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        for (final Rib rib : Rib.values()) {
            Ribs.INSTANCE.cartilage(s, rib, female);
        }
    }
}

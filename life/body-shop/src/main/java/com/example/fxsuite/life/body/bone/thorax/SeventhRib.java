package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The seventh rib, one a side, drawn in the body's frame at rest and moving with the chest: from its vertebra round to
 * the front, its cartilage reaching the sternum.
 */
public record SeventhRib() implements Bone {

    public static final SeventhRib INSTANCE = new SeventhRib();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<SeventhRib> {
        VERTEBRA("its head, on its vertebra", () -> ThoraxContacts.INSTANCE.costovertebral(Rib.SEVENTH)),
        CARTILAGE("its end, on its cartilage", () -> ThoraxContacts.INSTANCE.costochondral(Rib.SEVENTH));

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
        return "seventh rib";
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
        return 0.1;
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
    public void draw(final Sketch s, final double female) {
        Ribs.INSTANCE.draw(s, Rib.SEVENTH, female, 1 - 0.12 * female);
    }
}

package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The twelfth rib, one a side, drawn in the body's frame at rest and moving with the chest: from its vertebra round to
 * the front, floating, its cartilage reaching nothing.
 */
public record TwelfthRib() implements Bone {

    public static final TwelfthRib INSTANCE = new TwelfthRib();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<TwelfthRib> {
        VERTEBRA("its head, on its vertebra", () -> ThoraxContacts.INSTANCE.costovertebral(Rib.TWELFTH)),
        CARTILAGE("its end, on its cartilage", () -> ThoraxContacts.INSTANCE.costochondral(Rib.TWELFTH));

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
        return "twelfth rib";
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
        Ribs.INSTANCE.draw(s, Rib.TWELFTH, female, 1 - 0.12 * female);
    }
}

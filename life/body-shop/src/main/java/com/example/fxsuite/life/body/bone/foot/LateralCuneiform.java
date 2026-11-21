package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The outer wedge, beside the cuboid, under the third metatarsal's base.
 */
public record LateralCuneiform() implements Bone {

    public static final LateralCuneiform INSTANCE = new LateralCuneiform();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<LateralCuneiform> {
        NAVICULAR("its back, on the navicular",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.LATERAL)),
        INTERMEDIATE("its inner side, on the intermediate cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.INTERMEDIATE, FootContacts.LATERAL)),
        CUBOID("its outer side, on the cuboid",
                () -> FootContacts.INSTANCE.between(FootContacts.LATERAL, FootContacts.CUBOID)),
        THIRD("its front, under the third metatarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.THIRD));

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
        return "lateral cuneiform";
    }

    @Override
    public Region region() {
        return Region.FOOT;
    }

    @Override
    public Part part() {
        return Part.RIGHT_FOOT;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.5).egg(v(0.7, -2.85, 6.4), 0.6, 1.0, 0.7);
    }
}

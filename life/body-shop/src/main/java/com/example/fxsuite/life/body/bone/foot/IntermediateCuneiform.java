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
 * The smallest wedge, between the other two, under the second metatarsal's base.
 */
public record IntermediateCuneiform() implements Bone {

    public static final IntermediateCuneiform INSTANCE = new IntermediateCuneiform();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<IntermediateCuneiform> {
        NAVICULAR("its back, on the navicular",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.INTERMEDIATE)),
        MEDIAL("its inner side, on the medial cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.MEDIAL, FootContacts.INTERMEDIATE)),
        LATERAL("its outer side, on the lateral cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.INTERMEDIATE, FootContacts.LATERAL)),
        SECOND("its front, under the second metatarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.SECOND));

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
        return "intermediate cuneiform";
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
        s.blend(0.5).egg(v(-0.5, -2.65, 6.35), 0.58, 0.95, 0.62);
    }
}

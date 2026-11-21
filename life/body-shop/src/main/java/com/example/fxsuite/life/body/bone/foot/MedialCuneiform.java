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
 * The largest wedge, before the navicular on the inner side, under the first metatarsal's base.
 */
public record MedialCuneiform() implements Bone {

    public static final MedialCuneiform INSTANCE = new MedialCuneiform();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<MedialCuneiform> {
        NAVICULAR("its back, on the navicular",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.MEDIAL)),
        INTERMEDIATE("its outer side, on the intermediate cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.MEDIAL, FootContacts.INTERMEDIATE)),
        FIRST("its front, under the first metatarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.GREAT));

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
        return "medial cuneiform";
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
        s.blend(0.5).egg(v(-1.9, -3.3, 6.45), 0.75, 1.25, 0.75);
    }
}

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
 * The boat-shaped bone before the talus's head, on the inner side of the foot, its tuberosity standing out inside.
 */
public record Navicular() implements Bone {

    public static final Navicular INSTANCE = new Navicular();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Navicular> {
        TALUS("its back, on the talus's head",
                () -> FootContacts.INSTANCE.between(FootContacts.TALUS_HEAD, FootContacts.NAVICULAR)),
        MEDIAL("its front, on the medial cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.MEDIAL)),
        INTERMEDIATE("its front, on the intermediate cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.INTERMEDIATE)),
        LATERAL("its front, on the lateral cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.NAVICULAR, FootContacts.LATERAL));

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
        return "navicular";
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
        s.blend(0.5).egg(v(-1.1, -2.4, 5.05), 1.5, 1.1, 0.62, 0, 0, -15);
        s.ball(v(-2.35, -2.95, 4.75), 0.6);
        s.blend(0.3).carve().ball(v(-1.0, -2.3, 3.4), 1.25);
    }
}

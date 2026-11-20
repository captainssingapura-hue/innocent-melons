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
 * The ankle bone, in the foot's frame from the ankle joint: its rounded top in the mortise of the shin's bones, its
 * neck and head forward and in, toward the navicular.
 */
public record Talus() implements Bone {

    public static final Talus INSTANCE = new Talus();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Talus> {
        ANKLE("its trochlea, in the mortise",
                () -> com.example.fxsuite.life.body.bone.leg.LegContacts.INSTANCE.ankleOnTalus()),
        MALLEOLUS("its outer side, on the lateral malleolus",
                () -> com.example.fxsuite.life.body.bone.leg.LegContacts.INSTANCE.malleolusOnTalus()),
        CALCANEUS("its underside, on the heel bone", () -> FootContacts.INSTANCE.subtalar()),
        NAVICULAR("its head, on the navicular",
                () -> FootContacts.INSTANCE.between(FootContacts.TALUS_HEAD, FootContacts.NAVICULAR));

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
        return "talus";
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
        return 0.2;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.08 * female;
        // the trochlea, a pulley the shin's bones ride on
        s.blend(0.6).disc(v(0, -0.4, 0.3), v(1, 0, 0), 1.5, 1.3 * light, 0.55);
        s.blend(0.3).carve().ring(v(0, -0.4, 0.3), v(1, 0, 0), 2.0, 0.45);
        s.blend(0.7).egg(v(0.1, -1.1, 0.4), 1.5 * light, 0.85, 1.7);
        s.ball(v(0.2, -1.4, -1.6), 0.55);
        // the neck and the head
        s.rod(v(-0.2, -1.3, 1.6), v(-0.85, -2.1, 3.0), 0.95, 1.0);
        s.ball(v(-1.0, -2.3, 3.35), 1.12);
    }
}

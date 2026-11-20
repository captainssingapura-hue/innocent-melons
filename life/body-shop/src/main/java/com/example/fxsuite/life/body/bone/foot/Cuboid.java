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
 * The cube-shaped bone on the outer side of the foot, before the heel bone, grooved under for a tendon.
 */
public record Cuboid() implements Bone {

    public static final Cuboid INSTANCE = new Cuboid();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Cuboid> {
        CALCANEUS("its back, on the heel bone", () -> FootContacts.INSTANCE.calcaneocuboid()),
        LATERAL("its inner side, on the lateral cuneiform",
                () -> FootContacts.INSTANCE.between(FootContacts.LATERAL, FootContacts.CUBOID)),
        FOURTH("its front, under the fourth metatarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.FOURTH)),
        FIFTH("its front, under the fifth metatarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.LITTLE));

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
        return "cuboid";
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
        s.blend(0.6).egg(v(1.7, -3.65, 4.85), 1.15, 0.95, 1.35, 0, 0, 12);
        s.blend(0.3).carve().rod(v(1.2, -4.9, 5.2), v(2.9, -4.8, 4.6), 0.35, 0.35);
    }
}

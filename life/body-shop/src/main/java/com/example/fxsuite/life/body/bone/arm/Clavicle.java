package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The collarbone, drawn in the body's frame at rest and moving with the shoulder girdle: its round end at the top of
 * the sternum, bowed forward, then back, flattening out over the shoulder to the acromion.
 */
public record Clavicle() implements Bone {

    public static final Clavicle INSTANCE = new Clavicle();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Clavicle> {
        STERNUM("its sternal end", () -> ArmContacts.INSTANCE.sternoclavicular(Part.RIGHT_GIRDLE)),
        ACROMION("its acromial end", () -> ArmContacts.INSTANCE.acromioclavicular());

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
        return "clavicle";
    }

    @Override
    public Region region() {
        return Region.SHOULDER;
    }

    @Override
    public Part part() {
        return Part.RIGHT_GIRDLE;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        s.blend(0.6);
        s.egg(v(2.6, 45.3, 6.8), 1.0, 1.15 * light, 1.05);
        s.tube(1.0 * light, 0.72 * light, v(2.7, 45.4, 6.8), v(5.6, 45.7, 7.3), v(9.4, 46.3, 6.1));
        s.flat(v(9.4, 46.3, 6.1), v(13.4, 47.0, 3.4), 0.72 * light, 0.95 * light, v(0, 1, 0), 0.62);
        s.flat(v(13.4, 47.0, 3.4), v(16.4, 47.55, 1.4), 0.95 * light, 1.0 * light, v(0, 1, 0), 0.5);
        s.blend(0.4).ball(v(13.9, 46.5, 2.6), 0.45);
    }
}

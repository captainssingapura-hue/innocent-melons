package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.arm.ArmContacts;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The breastbone, drawn in the body's frame at rest and moving with the chest: its manubrium, notched at its top and at
 * either side for the clavicles; the angle where it meets the body; the body; the xiphoid below.
 */
public record Sternum() implements Bone {

    public static final Sternum INSTANCE = new Sternum();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Sternum> {
        CARTILAGES("where the cartilages reach it", () -> ThoraxContacts.INSTANCE.sternocostal()),
        CLAVICLE("its clavicular notch",
                () -> ArmContacts.INSTANCE.sternoclavicular(Part.CHEST));

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
        return "sternum";
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
        s.blend(0.5);
        s.plate(v(-2.7, 45.6, 6.6), v(2.7, 45.6, 6.6), v(1.5, 40.9, 7.4), v(-1.5, 40.9, 7.4), 1.1 * light);
        s.rod(v(-1.45, 40.85, 7.55), v(1.45, 40.85, 7.55), 0.45, 0.45);
        s.plate(v(-1.4, 40.9, 7.45), v(1.4, 40.9, 7.45), v(1.85, 35.5, 8.4), v(-1.85, 35.5, 8.4), 0.95 * light);
        s.plate(v(-1.85, 35.5, 8.4), v(1.85, 35.5, 8.4), v(1.5, 31.1, 9.2), v(-1.5, 31.1, 9.2), 0.9 * light);
        s.flat(v(0, 31.0, 9.2), v(0, 28.6, 9.0), 0.6, 0.25, v(0, 0, 1), 0.5);
        s.blend(0.3).carve().ball(v(0, 46.2, 6.6), 0.85);
        s.carve().ball(v(2.6, 45.3, 6.8), 1.12);
        s.carve().ball(v(-2.6, 45.3, 6.8), 1.12);
    }
}

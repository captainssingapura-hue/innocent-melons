package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The thigh bone, in the thigh's frame (x out to the side, y up, z ahead) from the hip joint, its head round it, to the
 * knee 42 cm below: its head, its neck turned forward, its trochanters, its shaft bowed forward, its condyles.
 */
public record Femur() implements Bone {

    public static final Femur INSTANCE = new Femur();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Femur> {
        HEAD("its head, in the acetabulum", () -> LegContacts.INSTANCE.hip()),
        KNEE("its condyles, on the tibia", () -> LegContacts.INSTANCE.knee()),
        GROOVE("its groove, under the kneecap", () -> LegContacts.INSTANCE.patellar());

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
        return "femur";
    }

    @Override
    public Region region() {
        return Region.LEG;
    }

    @Override
    public Part part() {
        return Part.RIGHT_THIGH;
    }

    @Override
    public double coarsest() {
        return 0.3;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.1 * female;
        // the head, round the hip joint, a pit in it where its ligament holds; the neck out and down to the shaft
        s.blend(0.6).ball(v(0, 0, 0), 2.4);
        s.blend(0.4).carve().ball(v(-2.35, -0.5, 0.35), 0.45);
        s.blend(0.7).flat(v(0.6, -0.4, -0.2), v(4.1, -2.8, -0.9), 1.55 * light, 1.8 * light, v(0, 0, 1), 0.78);
        // the greater trochanter out at the side, the hollow behind it; the lesser low and in behind
        s.egg(v(5.6, -2.8, -1.0), 1.35 * light, 2.2, 1.7 * light);
        s.blend(0.4).carve().egg(v(4.7, -1.9, -2.0), 0.6, 0.9, 0.6);
        s.blend(0.5).egg(v(2.9, -6.2, -1.25), 0.9, 0.95, 0.85);
        // the ridges between them, behind and before
        s.blend(0.4).rod(v(5.9, -1.4, -2.0), v(3.1, -6.0, -1.7), 0.55, 0.5);
        s.rod(v(5.2, -2.4, 0.7), v(3.0, -6.4, 0.45), 0.35, 0.3);
        // the shaft, down and in to the knee, bowed forward a little; the rough line down its back
        s.blend(0.8).tube(1.62 * light, 1.45 * light, v(4.8, -5.5, -0.5), v(3.7, -14, 0.25), v(2.4, -24, 0.45),
                v(1.2, -32.5, 0.15));
        s.blend(0.5).rod(v(3.5, -11, -1.25), v(1.9, -31, -1.35), 0.42, 0.42);
        knee(s, light);
    }

    /** Widening to the knee: its condyles, its epicondyles, the notch between them behind, the groove before. */
    private void knee(final Sketch s, final double light) {
        s.blend(1.2).egg(v(0.35, -37.0, -0.2), 2.7 * light, 3.0, 2.15 * light);
        s.blend(0.9).egg(v(-2.2, -41.0, -0.7), 1.55, 2.05, 2.75);
        s.egg(v(2.3, -40.8, -0.45), 1.55, 2.0, 2.65);
        s.ball(v(-3.5, -39.6, -0.4), 0.9);
        s.ball(v(-3.3, -38.0, -0.9), 0.45);
        s.ball(v(3.55, -39.8, -0.2), 0.8);
        s.blend(0.45).carve().egg(v(0, -42.1, -1.7), 0.95, 1.7, 1.9);
        s.carve().rod(v(0, -37.6, 2.45), v(0, -40.9, 2.35), 0.7, 0.75);
    }
}

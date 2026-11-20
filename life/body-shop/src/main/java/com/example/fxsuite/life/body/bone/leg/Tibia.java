package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The shin bone, in the shin's frame from the knee to the ankle 42 cm below: its plateau under the femur's condyles,
 * its tuberosity, its crest sharp down the front, its medial malleolus at the ankle.
 */
public record Tibia() implements Bone {

    public static final Tibia INSTANCE = new Tibia();

    @Override
    public String name() {
        return "tibia";
    }

    @Override
    public Region region() {
        return Region.LEG;
    }

    @Override
    public Part part() {
        return Part.RIGHT_SHIN;
    }

    @Override
    public double coarsest() {
        return 0.3;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.1 * female;
        // the plateau, a shallow hollow on each side, the eminence rising between them
        s.blend(0.7).egg(v(0, -2.45, -0.1), 3.75 * light, 1.45, 2.6 * light);
        s.blend(0.5).carve().egg(v(-1.9, -0.25, -0.1), 1.6, 1.0, 1.7);
        s.carve().egg(v(1.9, -0.25, -0.2), 1.5, 1.0, 1.6);
        s.blend(0.3).rod(v(-0.45, -1.4, 0), v(-0.35, -0.65, 0), 0.45, 0.2);
        s.rod(v(0.45, -1.4, -0.1), v(0.35, -0.7, -0.1), 0.45, 0.2);
        // the tuberosity at the front, Gerdy's tubercle at the side
        s.blend(0.8).egg(v(0, -5.8, 2.55), 1.2, 1.7, 0.9);
        s.blend(0.5).ball(v(2.4, -3.6, 1.35), 0.6);
        // the shaft, three-sided: its crest sharp at the front
        s.blend(0.9).tube(2.0 * light, 1.2 * light, v(0, -4, 0.2), v(0, -14, 0.4), v(-0.1, -26, 0.3),
                v(-0.3, -35, 0.2));
        s.blend(0.6).rod(v(0, -7, 1.9), v(0, -32, 1.2), 0.45, 0.4);
        // widening to the ankle, and the medial malleolus down its inner side
        s.blend(1.0).egg(v(-0.3, -38.6, 0.1), 2.0 * light, 2.2, 1.8 * light);
        s.blend(0.6).rod(v(-1.9, -39.4, 0.3), v(-2.0, -41.6, 0.1), 1.0, 0.65);
    }
}

package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The calf bone, thin, at the outer side of the shin, in its frame: its head below the knee, its shaft, its lateral
 * malleolus reaching lowest at the ankle.
 */
public record Fibula() implements Bone {

    public static final Fibula INSTANCE = new Fibula();

    @Override
    public String name() {
        return "fibula";
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
        return 0.2;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.1 * female;
        s.blend(0.5).egg(v(2.9, -4.0, -1.4), 1.0, 1.0, 1.0);
        s.rod(v(3.0, -3.6, -1.6), v(3.15, -2.9, -1.75), 0.55, 0.3);
        s.blend(0.6).tube(0.68 * light, 0.5 * light, v(2.9, -4.6, -1.3), v(2.85, -7, -1.1), v(2.9, -22, -0.8));
        s.tube(0.5 * light, 0.65 * light, v(2.9, -22, -0.8), v(2.65, -37, -0.3));
        s.blend(0.8).egg(v(2.7, -41.3, -0.5), 0.85, 1.7, 1.0);
    }
}

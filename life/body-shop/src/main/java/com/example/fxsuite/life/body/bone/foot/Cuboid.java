package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The cube-shaped bone on the outer side of the foot, before the heel bone, grooved under for a tendon.
 */
public record Cuboid() implements Bone {

    public static final Cuboid INSTANCE = new Cuboid();

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

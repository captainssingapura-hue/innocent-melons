package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The boat-shaped bone before the talus's head, on the inner side of the foot, its tuberosity standing out inside.
 */
public record Navicular() implements Bone {

    public static final Navicular INSTANCE = new Navicular();

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

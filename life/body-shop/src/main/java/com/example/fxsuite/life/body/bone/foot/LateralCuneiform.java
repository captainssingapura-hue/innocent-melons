package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The outer wedge, beside the cuboid, under the third metatarsal's base.
 */
public record LateralCuneiform() implements Bone {

    public static final LateralCuneiform INSTANCE = new LateralCuneiform();

    @Override
    public String name() {
        return "lateral cuneiform";
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
        s.blend(0.5).egg(v(0.7, -2.85, 6.4), 0.6, 1.0, 0.7);
    }
}

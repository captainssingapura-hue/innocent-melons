package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The smallest wedge, between the other two, under the second metatarsal's base.
 */
public record IntermediateCuneiform() implements Bone {

    public static final IntermediateCuneiform INSTANCE = new IntermediateCuneiform();

    @Override
    public String name() {
        return "intermediate cuneiform";
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
        s.blend(0.5).egg(v(-0.5, -2.65, 6.35), 0.58, 0.95, 0.62);
    }
}

package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The largest wedge, before the navicular on the inner side, under the first metatarsal's base.
 */
public record MedialCuneiform() implements Bone {

    public static final MedialCuneiform INSTANCE = new MedialCuneiform();

    @Override
    public String name() {
        return "medial cuneiform";
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
        s.blend(0.5).egg(v(-1.9, -3.3, 6.45), 0.75, 1.25, 0.75);
    }
}

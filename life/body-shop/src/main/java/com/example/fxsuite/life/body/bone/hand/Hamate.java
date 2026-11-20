package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The carpal of the second row on the little finger's side, its hook toward the palm.
 */
public record Hamate() implements Bone {

    public static final Hamate INSTANCE = new Hamate();

    @Override
    public String name() {
        return "hamate";
    }

    @Override
    public Region region() {
        return Region.HAND;
    }

    @Override
    public Part part() {
        return Part.RIGHT_HAND;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        s.blend(0.4);
        s.egg(v(-1.45, -2.65, 0.0), 0.75 * light, 0.95, 0.75);
        s.rod(v(-1.4, -2.8, 0.5), v(-1.3, -2.95, 1.2), 0.38, 0.26);
    }
}

package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The largest carpal, in the middle of the second row, its round head up in the lunate's hollow.
 */
public record Capitate() implements Bone {

    public static final Capitate INSTANCE = new Capitate();

    @Override
    public String name() {
        return "capitate";
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
        s.egg(v(-0.1, -2.75, -0.05), 0.72 * light, 1.1, 0.8);
        s.ball(v(-0.05, -1.85, -0.05), 0.62);
    }
}

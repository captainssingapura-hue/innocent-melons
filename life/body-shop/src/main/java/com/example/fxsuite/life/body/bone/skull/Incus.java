package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The anvil, one a side, between the hammer and the stirrup in the middle ear.
 */
public record Incus() implements Bone {

    public static final Incus INSTANCE = new Incus();

    @Override
    public String name() {
        return "incus";
    }

    @Override
    public Region region() {
        return Region.EAR;
    }

    @Override
    public Part part() {
        return Part.HEAD;
    }

    @Override
    public double coarsest() {
        return 0.02;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.04).ball(v(4.85, 2.0, -0.62), 0.15);
        s.rod(v(4.85, 1.95, -0.62), v(4.9, 1.38, -0.55), 0.06, 0.05);
        s.rod(v(4.85, 2.0, -0.7), v(4.8, 1.95, -1.0), 0.07, 0.04);
    }
}

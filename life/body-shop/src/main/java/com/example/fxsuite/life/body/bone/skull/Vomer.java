package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The vomer, a thin blade down the middle of the nose's cavity, the back of its septum.
 */
public record Vomer() implements Bone {

    public static final Vomer INSTANCE = new Vomer();

    @Override
    public String name() {
        return "vomer";
    }

    @Override
    public Region region() {
        return Region.SKULL;
    }

    @Override
    public Part part() {
        return Part.HEAD;
    }

    @Override
    public double coarsest() {
        return 0.05;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.2).plate(v(0, 0.5, 4.6), v(0, 3.6, 6.0), v(0, 0.6, 8.6), 0.2);
    }
}

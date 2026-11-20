package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Moves;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The kneecap, in the thigh's frame where it sits with the knee straight, before the femur's groove above the joint:
 * rounded above, coming to a point below, its back ridged; riding down the front of the knee as it bends.
 */
public record Patella() implements Bone {

    public static final Patella INSTANCE = new Patella();

    /** Where its middle is on the thigh, the knee straight. */
    public static final V3 MIDDLE = v(0, -39.6, 4.1);

    @Override
    public String name() {
        return "patella";
    }

    @Override
    public Region region() {
        return Region.LEG;
    }

    @Override
    public Part part() {
        return Part.RIGHT_THIGH;
    }

    @Override
    public Moves moves() {
        return Moves.KNEECAP;
    }

    @Override
    public double coarsest() {
        return 0.2;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.8);
        s.egg(MIDDLE.plus(v(0, 0.35, 0)), 2.15, 1.9, 0.95);
        s.egg(MIDDLE.plus(v(0, -1.2, 0.1)), 1.3, 1.25, 0.8);
        s.blend(0.4).carve().egg(MIDDLE.plus(v(-0.9, 0, -1.25)), 1.0, 1.6, 0.55);
        s.carve().egg(MIDDLE.plus(v(0.95, 0, -1.25)), 1.1, 1.6, 0.55);
    }
}

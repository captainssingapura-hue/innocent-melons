package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The occipital bone: the back of the vault behind the lambdoid suture, and the base round the foramen magnum, the
 * condyles the head nods on.
 */
public record Occipital() implements Bone {

    public static final Occipital INSTANCE = new Occipital();

    @Override
    public String name() {
        return "occipital bone";
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
        return 0.12;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final Cuts c = Cuts.INSTANCE;
        c.cut(s, female, c.any(c.behindLambdoid(s), c.all(c.below(s, 3.0), c.behind(s, 1.0), c.within(s, 3.5))));
    }
}

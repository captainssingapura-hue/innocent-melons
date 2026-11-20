package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * A parietal bone, one a side: the top and the side of the vault, between the coronal and the lambdoid sutures, above
 * the temporal, its fellow beyond the middle.
 */
public record Parietal() implements Bone {

    public static final Parietal INSTANCE = new Parietal();

    @Override
    public String name() {
        return "parietal bone";
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
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final Cuts c = Cuts.INSTANCE;
        c.cut(s, female, c.all(c.behindCoronal(s), c.aheadOfLambdoid(s), c.above(s, 6.0), c.right(s)));
    }
}

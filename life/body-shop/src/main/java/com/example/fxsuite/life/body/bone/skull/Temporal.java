package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * A temporal bone, one a side: the side of the skull low about the ear, its ear canal and middle ear, its mastoid, its
 * styloid, the back of the cheek's arch and the hollow the jaw turns in.
 */
public record Temporal() implements Bone {

    public static final Temporal INSTANCE = new Temporal();

    @Override
    public String name() {
        return "temporal bone";
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
        c.cut(s, female, c.all(c.outside(s, 3.5), c.below(s, 6.0), c.behind(s, 3.0), c.aheadOfLambdoid(s)));
    }
}

package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * A nasal bone, one a side of the bridge of the nose, over its opening.
 */
public record Nasal() implements Bone {

    public static final Nasal INSTANCE = new Nasal();

    @Override
    public String name() {
        return "nasal bone";
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
        c.cut(s, female, c.all(c.right(s), c.within(s, 1.2), c.above(s, 3.9), c.ahead(s, 9.0)));
    }
}

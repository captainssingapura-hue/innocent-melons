package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * A lacrimal bone, one a side, small, in the front of the orbit's inner wall.
 */
public record Lacrimal() implements Bone {

    public static final Lacrimal INSTANCE = new Lacrimal();

    @Override
    public String name() {
        return "lacrimal bone";
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
        c.cut(s, female, c.all(c.outside(s, 1.0), c.within(s, 2.0), c.above(s, 3.6), c.below(s, 5.4), c.ahead(s, 6.6),
                c.behind(s, 8.2)));
    }
}

package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The sphenoid bone: the middle of the skull's base, before the occipital, its greater wings out to the temples, its
 * pterygoid plates down behind the upper jaw.
 */
public record Sphenoid() implements Bone {

    public static final Sphenoid INSTANCE = new Sphenoid();

    @Override
    public String name() {
        return "sphenoid bone";
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
        c.cut(s, female, c.any(c.all(c.within(s, 3.5), c.ahead(s, 1.0), c.behind(s, 5.0), c.below(s, 4.5)),
                c.all(c.but(c.within(s, 5.8), c.within(s, 3.5)), c.ahead(s, 3.0), c.behind(s, 4.6), c.above(s, 2.5),
                        c.below(s, 6.0))));
    }
}

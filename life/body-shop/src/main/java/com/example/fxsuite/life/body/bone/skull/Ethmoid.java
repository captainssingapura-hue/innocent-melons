package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The ethmoid bone, between the orbits behind the nose's bones: the inner walls of the orbits, the roof of the nose's
 * cavity.
 */
public record Ethmoid() implements Bone {

    public static final Ethmoid INSTANCE = new Ethmoid();

    @Override
    public String name() {
        return "ethmoid bone";
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
        c.cut(s, female, c.all(c.within(s, 1.0), c.above(s, 3.4), c.below(s, 6.4), c.ahead(s, 5.0), c.behind(s, 9.0)));
    }
}

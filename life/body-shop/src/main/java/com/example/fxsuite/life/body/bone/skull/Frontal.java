package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The frontal bone: the forehead, the brows, the roofs of the orbits; the skull cut down to ahead of the coronal suture
 * and above the orbits.
 */
public record Frontal() implements Bone {

    public static final Frontal INSTANCE = new Frontal();

    @Override
    public String name() {
        return "frontal bone";
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
        c.cut(s, female, c.all(c.aheadOfCoronal(s), c.above(s, 5.9)));
    }
}

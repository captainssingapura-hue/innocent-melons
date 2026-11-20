package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The twelfth rib, one a side, drawn in the body's frame at rest and moving with the chest: from its vertebra round to
 * the front, floating, its cartilage reaching nothing.
 */
public record TwelfthRib() implements Bone {

    public static final TwelfthRib INSTANCE = new TwelfthRib();

    @Override
    public String name() {
        return "twelfth rib";
    }

    @Override
    public Region region() {
        return Region.THORAX;
    }

    @Override
    public Part part() {
        return Part.CHEST;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Ribs.INSTANCE.draw(s, Rib.TWELFTH, 1 - 0.05 * female, 1 - 0.12 * female);
    }
}

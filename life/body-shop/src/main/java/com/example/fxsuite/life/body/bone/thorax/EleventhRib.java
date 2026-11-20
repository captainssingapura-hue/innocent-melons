package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The eleventh rib, one a side, drawn in the body's frame at rest and moving with the chest: from its vertebra round to
 * the front, floating, its cartilage reaching nothing.
 */
public record EleventhRib() implements Bone {

    public static final EleventhRib INSTANCE = new EleventhRib();

    @Override
    public String name() {
        return "eleventh rib";
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
        Ribs.INSTANCE.draw(s, Rib.ELEVENTH, 1 - 0.05 * female, 1 - 0.12 * female);
    }
}

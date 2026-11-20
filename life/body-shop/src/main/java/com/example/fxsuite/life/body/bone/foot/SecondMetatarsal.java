package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The second metatarsal, in the foot's frame: from its base in the arch to its head the foot stands on.
 */
public record SecondMetatarsal() implements Bone {

    public static final SecondMetatarsal INSTANCE = new SecondMetatarsal();

    @Override
    public String name() {
        return "second metatarsal";
    }

    @Override
    public Region region() {
        return Region.FOOT;
    }

    @Override
    public Part part() {
        return Part.RIGHT_FOOT;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Toes.INSTANCE.metatarsal(s, Toe.SECOND, 1 - 0.08 * female);
    }
}

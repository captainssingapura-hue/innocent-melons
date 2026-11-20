package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.bone.Kind;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The sixteen upper teeth, set in the maxillae's arch, meeting the lower in a bite. Not bones, so not counted.
 */
public record UpperTeeth() implements Bone {

    public static final UpperTeeth INSTANCE = new UpperTeeth();

    @Override
    public String name() {
        return "upper teeth";
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
        return 0.05;
    }

    @Override
    public Kind kind() {
        return Kind.TEETH;
    }

    @Override
    public Paint paint() {
        return Paint.TEETH;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Teeth.INSTANCE.draw(s, true);
    }
}

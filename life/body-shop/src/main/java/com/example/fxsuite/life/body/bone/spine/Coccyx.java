package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The tailbone, four small bones grown together, drawn in the body's frame at rest and moving with the pelvis, curving
 * on down and forward from the sacrum's apex.
 */
public record Coccyx() implements Bone {

    public static final Coccyx INSTANCE = new Coccyx();

    @Override
    public String name() {
        return "coccyx";
    }

    @Override
    public Region region() {
        return Region.SPINE;
    }

    @Override
    public Part part() {
        return Part.PELVIS;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public double coarsest() {
        return 0.08;
    }

    /** Where it meets the sacrum, at its apex. */
    public enum Points implements Point<Coccyx> {
        ABOVE;

        @Override
        public String said() {
            return "on the sacrum's apex";
        }

        @Override
        public Place frame() {
            return Discs.INSTANCE.inPart(Discs.INSTANCE.sacrococcygeal(), Part.PELVIS);
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.35);
        s.egg(v(0, 0.85, -7.05), 0.75, 0.45, 0.42);
        s.egg(v(0, 0.1, -6.75), 0.55, 0.38, 0.36);
        s.egg(v(0, -0.55, -6.35), 0.42, 0.33, 0.3);
        s.ball(v(0, -1.1, -5.9), 0.25);
    }
}

package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The third cervical vertebra, drawn in the body's frame at rest and moving with the neck, as big as its level has it;
 * meeting the vertebra above and the one below on the discs between them.
 */
public record C3() implements Bone {

    public static final C3 INSTANCE = new C3();

    @Override
    public String name() {
        return "third cervical vertebra (C3)";
    }

    @Override
    public Region region() {
        return Region.SPINE;
    }

    @Override
    public Part part() {
        return Part.NECK;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    /** Where it meets its neighbours, each a frame in the frame it is drawn in. */
    public enum Points implements Point<C3> {
        ABOVE("on the disc above"),
        BELOW("on the disc below");

        private final String said;

        Points(final String said) {
            this.said = said;
        }

        @Override
        public String said() {
            return said;
        }

        @Override
        public Place frame() {
            final Discs d = Discs.INSTANCE;
            return d.inPart(this == ABOVE ? d.below(Level.C2) : d.below(Level.C3), Part.NECK);
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Vertebrae.INSTANCE.draw(s, Level.C3, 1 - 0.1 * female);
    }
}

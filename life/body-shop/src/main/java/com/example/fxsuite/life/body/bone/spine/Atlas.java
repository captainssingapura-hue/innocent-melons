package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The first cervical vertebra, under the skull, drawn in the body's frame at rest and moving with the neck: no body, a
 * ring of bone round the axis's dens and the cord, its lateral masses holding up the skull's condyles; turning on the
 * dens.
 */
public record Atlas() implements Bone {

    public static final Atlas INSTANCE = new Atlas();

    @Override
    public String name() {
        return "atlas (C1)";
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
    public enum Points implements Point<Atlas> {
        ABOVE("under the skull, where the head nods"),
        BELOW("round the axis's dens");

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
            return d.inPart(this == ABOVE ? d.head() : d.pivot(), Part.NECK);
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Vertebrae.INSTANCE.atlas(s, 1 - 0.1 * female);
    }
}

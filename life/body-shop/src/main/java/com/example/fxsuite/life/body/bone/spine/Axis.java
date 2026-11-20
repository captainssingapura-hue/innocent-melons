package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The second cervical vertebra, drawn in the body's frame at rest and moving with the neck, its dens rising up through
 * the atlas's ring, the pivot the head turns on.
 */
public record Axis() implements Bone {

    public static final Axis INSTANCE = new Axis();

    @Override
    public String name() {
        return "axis (C2)";
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
    public enum Points implements Point<Axis> {
        ABOVE("its dens, the atlas turning round it"),
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
            return d.inPart(this == ABOVE ? d.pivot() : d.below(Level.C2), Part.NECK);
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Vertebrae.INSTANCE.draw(s, Level.C2, 1 - 0.1 * female);
        Vertebrae.INSTANCE.dens(s);
    }
}

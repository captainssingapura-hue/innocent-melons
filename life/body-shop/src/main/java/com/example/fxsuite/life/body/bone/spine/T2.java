package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The second thoracic vertebra, drawn in the body's frame at rest and moving with the chest, as big as its level has
 * it; meeting the vertebra above and the one below on the discs between them; its ribs at their heads.
 */
public record T2() implements Bone {

    public static final T2 INSTANCE = new T2();

    @Override
    public String name() {
        return "second thoracic vertebra (T2)";
    }

    @Override
    public Region region() {
        return Region.SPINE;
    }

    @Override
    public Part part() {
        return Part.CHEST;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    /** Where it meets its neighbours, each a frame in the frame it is drawn in. */
    public enum Points implements Point<T2> {
        ABOVE("on the disc above"),
        BELOW("on the disc below"),
        RIB("its rib's head, beside its body");

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
            return switch (this) {
                case ABOVE -> d.inPart(d.below(Level.T1), Part.CHEST);
                case BELOW -> d.inPart(d.below(Level.T2), Part.CHEST);
                case RIB -> d.inPart(d.rib(Level.T2), Part.CHEST);
            };
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Vertebrae.INSTANCE.draw(s, Level.T2, 1 - 0.1 * female);
    }
}

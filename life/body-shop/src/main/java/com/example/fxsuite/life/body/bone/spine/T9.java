package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The ninth thoracic vertebra, drawn in the body's frame at rest and moving with the chest, as big as its level has it;
 * meeting the vertebra above and the one below on the discs between them; its ribs at their heads.
 */
public record T9() implements Bone {

    public static final T9 INSTANCE = new T9();

    @Override
    public String name() {
        return "ninth thoracic vertebra (T9)";
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
    public enum Points implements Point<T9> {
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
                case ABOVE -> d.inPart(d.below(Level.T8), Part.CHEST);
                case BELOW -> d.inPart(d.below(Level.T9), Part.CHEST);
                case RIB -> d.inPart(d.rib(Level.T9), Part.CHEST);
            };
        }
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Vertebrae.INSTANCE.draw(s, Level.T9, 1 - 0.1 * female);
    }
}

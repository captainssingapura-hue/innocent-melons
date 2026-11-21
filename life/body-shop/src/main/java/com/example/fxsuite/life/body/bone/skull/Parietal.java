package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A parietal bone, one a side: the top and the side of the vault, between the coronal and the lambdoid sutures, above
 * the temporal, its fellow beyond the middle.
 */
public record Parietal() implements Bone {

    public static final Parietal INSTANCE = new Parietal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Parietal> {
        FRONTAL("the coronal suture, to the frontal",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.PARIETAL)),
        PARIETAL("the sagittal suture, to its fellow", () -> SkullContacts.INSTANCE.middle(SkullContacts.PARIETAL)),
        OCCIPITAL("the lambdoid suture, to the occipital",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.PARIETAL)),
        TEMPORAL("the squamous suture, to the temporal",
                () -> SkullContacts.INSTANCE.between(SkullContacts.PARIETAL, SkullContacts.TEMPORAL)),
        SPHENOID("to the sphenoid's wing",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.PARIETAL));

        private final String said;
        private final Supplier<Place> frame;

        Points(final String said, final Supplier<Place> frame) {
            this.said = said;
            this.frame = frame;
        }

        @Override
        public String said() {
            return said;
        }

        @Override
        public Place frame() {
            return frame.get();
        }
    }

    @Override
    public String name() {
        return "parietal bone";
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
        return 0.12;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final Cuts c = Cuts.INSTANCE;
        c.cut(s, female, c.all(c.behindCoronal(s), c.aheadOfLambdoid(s), c.above(s, 6.0), c.right(s)));
    }
}

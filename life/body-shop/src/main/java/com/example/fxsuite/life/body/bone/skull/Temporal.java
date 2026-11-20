package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A temporal bone, one a side: the side of the skull low about the ear, its ear canal and middle ear, its mastoid, its
 * styloid, the back of the cheek's arch and the hollow the jaw turns in.
 */
public record Temporal() implements Bone {

    public static final Temporal INSTANCE = new Temporal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Temporal> {
        PARIETAL("the squamous suture, to the parietal",
                () -> SkullContacts.INSTANCE.between(SkullContacts.PARIETAL, SkullContacts.TEMPORAL)),
        OCCIPITAL("the occipitomastoid suture, to the occipital",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.TEMPORAL)),
        SPHENOID("to the sphenoid's wing",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.TEMPORAL)),
        ZYGOMATIC("its arch, to the zygomatic's",
                () -> SkullContacts.INSTANCE.between(SkullContacts.TEMPORAL, SkullContacts.ZYGOMATIC)),
        MANDIBLE("the hollow the jaw turns in", () -> SkullContacts.INSTANCE.jaw()),
        STAPES("the inner ear's window, the stirrup in it",
                () -> SkullContacts.INSTANCE.between(SkullContacts.STAPES, SkullContacts.OVAL_WINDOW));

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
        return "temporal bone";
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
        c.cut(s, female, c.all(c.outside(s, 3.5), c.below(s, 6.0), c.behind(s, 3.0), c.aheadOfLambdoid(s)));
    }
}

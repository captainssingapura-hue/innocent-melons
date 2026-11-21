package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The occipital bone: the back of the vault behind the lambdoid suture, and the base round the foramen magnum, the
 * condyles the head nods on.
 */
public record Occipital() implements Bone {

    public static final Occipital INSTANCE = new Occipital();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Occipital> {
        ATLAS("its condyles, on the atlas", () -> SkullContacts.INSTANCE.atlas()),
        PARIETAL("the lambdoid suture, to the parietals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.PARIETAL)),
        TEMPORAL("the occipitomastoid suture, to the temporals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.TEMPORAL)),
        SPHENOID("its base, on the sphenoid's body",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.SPHENOID));

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
        return "occipital bone";
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
    public void draw(final Sketch s, final double female) {
        final Cuts c = Cuts.INSTANCE;
        c.cut(s, female, c.any(c.behindLambdoid(s), c.all(c.below(s, 3.0), c.behind(s, 1.0), c.within(s, 3.5))));
    }
}

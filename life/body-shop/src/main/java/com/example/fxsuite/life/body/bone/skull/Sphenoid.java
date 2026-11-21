package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The sphenoid bone: the middle of the skull's base, before the occipital, its greater wings out to the temples, its
 * pterygoid plates down behind the upper jaw.
 */
public record Sphenoid() implements Bone {

    public static final Sphenoid INSTANCE = new Sphenoid();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Sphenoid> {
        OCCIPITAL("its body, on the occipital's base",
                () -> SkullContacts.INSTANCE.between(SkullContacts.OCCIPITAL, SkullContacts.SPHENOID)),
        FRONTAL("its wings, to the frontal",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.SPHENOID)),
        TEMPORAL("its greater wings, to the temporals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.TEMPORAL)),
        PARIETAL("its greater wings, to the parietals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.PARIETAL)),
        ETHMOID("to the ethmoid", () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.ETHMOID)),
        PALATINE("its plates, to the palatines",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.PALATINE)),
        VOMER("its body, on the vomer",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.VOMER));

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
        return "sphenoid bone";
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
        c.cut(s, female, c.any(c.all(c.within(s, 3.5), c.ahead(s, 1.0), c.behind(s, 5.0), c.below(s, 4.5)),
                c.all(c.but(c.within(s, 5.8), c.within(s, 3.5)), c.ahead(s, 3.0), c.behind(s, 4.6), c.above(s, 2.5),
                        c.below(s, 6.0))));
    }
}

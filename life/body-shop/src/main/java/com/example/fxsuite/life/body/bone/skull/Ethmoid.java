package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The ethmoid bone, between the orbits behind the nose's bones: the inner walls of the orbits, the roof of the nose's
 * cavity.
 */
public record Ethmoid() implements Bone {

    public static final Ethmoid INSTANCE = new Ethmoid();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Ethmoid> {
        FRONTAL("to the frontal", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.ETHMOID)),
        SPHENOID("to the sphenoid",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.ETHMOID)),
        LACRIMAL("to the lacrimals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.ETHMOID, SkullContacts.LACRIMAL)),
        VOMER("to the vomer", () -> SkullContacts.INSTANCE.between(SkullContacts.ETHMOID, SkullContacts.VOMER));

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
        return "ethmoid bone";
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
        c.cut(s, female, c.all(c.within(s, 1.0), c.above(s, 3.4), c.below(s, 6.4), c.ahead(s, 5.0), c.behind(s, 9.0)));
    }
}

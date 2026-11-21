package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The frontal bone: the forehead, the brows, the roofs of the orbits; the skull cut down to ahead of the coronal suture
 * and above the orbits.
 */
public record Frontal() implements Bone {

    public static final Frontal INSTANCE = new Frontal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Frontal> {
        PARIETAL("the coronal suture, to the parietals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.PARIETAL)),
        SPHENOID("to the sphenoid's wings",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.SPHENOID)),
        ETHMOID("to the ethmoid, between the orbits",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.ETHMOID)),
        NASAL("to the nasal bones", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.NASAL)),
        MAXILLA("to the maxillae", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.MAXILLA)),
        ZYGOMATIC("to the zygomatics",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.ZYGOMATIC)),
        LACRIMAL("to the lacrimals",
                () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.LACRIMAL));

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
        return "frontal bone";
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
        c.cut(s, female, c.all(c.aheadOfCoronal(s), c.above(s, 5.9)));
    }
}

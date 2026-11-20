package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A zygomatic bone, one a side: the cheekbone under the orbit, the front of the arch back to the ear.
 */
public record Zygomatic() implements Bone {

    public static final Zygomatic INSTANCE = new Zygomatic();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Zygomatic> {
        FRONTAL("to the frontal", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.ZYGOMATIC)),
        MAXILLA("to the maxilla", () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.ZYGOMATIC)),
        TEMPORAL("its arch, to the temporal's",
                () -> SkullContacts.INSTANCE.between(SkullContacts.TEMPORAL, SkullContacts.ZYGOMATIC));

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
        return "zygomatic bone";
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
        c.cut(s, female, c.all(c.outside(s, 4.0), c.ahead(s, 3.0), c.above(s, 1.6), c.below(s, 5.9),
                c.any(c.outside(s, 5.6), c.ahead(s, 4.6))));
    }
}

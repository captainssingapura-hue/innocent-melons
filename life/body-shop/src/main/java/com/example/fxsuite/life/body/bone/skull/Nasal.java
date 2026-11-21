package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A nasal bone, one a side of the bridge of the nose, over its opening.
 */
public record Nasal() implements Bone {

    public static final Nasal INSTANCE = new Nasal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Nasal> {
        FRONTAL("to the frontal", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.NASAL)),
        NASAL("to its fellow", () -> SkullContacts.INSTANCE.middle(SkullContacts.NASAL)),
        MAXILLA("to the maxilla", () -> SkullContacts.INSTANCE.between(SkullContacts.NASAL, SkullContacts.MAXILLA));

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
        return "nasal bone";
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
        c.cut(s, female, c.all(c.right(s), c.within(s, 1.2), c.above(s, 3.9), c.ahead(s, 9.0)));
    }
}

package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A lacrimal bone, one a side, small, in the front of the orbit's inner wall.
 */
public record Lacrimal() implements Bone {

    public static final Lacrimal INSTANCE = new Lacrimal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Lacrimal> {
        FRONTAL("to the frontal", () -> SkullContacts.INSTANCE.between(SkullContacts.FRONTAL, SkullContacts.LACRIMAL)),
        MAXILLA("to the maxilla", () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.LACRIMAL)),
        ETHMOID("to the ethmoid", () -> SkullContacts.INSTANCE.between(SkullContacts.ETHMOID, SkullContacts.LACRIMAL));

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
        return "lacrimal bone";
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
        c.cut(s, female, c.all(c.outside(s, 1.0), c.within(s, 2.0), c.above(s, 3.6), c.below(s, 5.4), c.ahead(s, 6.6),
                c.behind(s, 8.2)));
    }
}

package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * A palatine bone, one a side, the back of the hard palate.
 */
public record Palatine() implements Bone {

    public static final Palatine INSTANCE = new Palatine();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Palatine> {
        MAXILLA("to the maxilla", () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.PALATINE)),
        PALATINE("to its fellow", () -> SkullContacts.INSTANCE.middle(SkullContacts.PALATINE)),
        SPHENOID("to the sphenoid",
                () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.PALATINE));

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
        return "palatine bone";
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
        c.cut(s, female, c.all(c.right(s), c.within(s, 2.6), c.above(s, -0.8), c.below(s, 1.2), c.ahead(s, 3.0),
                c.behind(s, 4.6)));
    }
}

package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The small carpal of the second row under the index finger's metacarpal.
 */
public record Trapezoid() implements Bone {

    public static final Trapezoid INSTANCE = new Trapezoid();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Trapezoid> {
        SCAPHOID("to the scaphoid", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.TRAPEZOID)),
        TRAPEZIUM("to the trapezium",
                () -> HandContacts.INSTANCE.between(HandContacts.TRAPEZIUM, HandContacts.TRAPEZOID)),
        CAPITATE("to the capitate", () -> HandContacts.INSTANCE.between(HandContacts.TRAPEZOID, HandContacts.CAPITATE)),
        SECOND("under the second metacarpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.INDEX));

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
        return "trapezoid";
    }

    @Override
    public Region region() {
        return Region.HAND;
    }

    @Override
    public Part part() {
        return Part.RIGHT_HAND;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        s.blend(0.4);
        s.egg(v(1.0, -2.85, 0.05), 0.55 * light, 0.7, 0.6);
    }
}

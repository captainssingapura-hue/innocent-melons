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
 * The boat-shaped carpal of the first row, on the thumb's side, under the radius.
 */
public record Scaphoid() implements Bone {

    public static final Scaphoid INSTANCE = new Scaphoid();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Scaphoid> {
        RADIUS("on the radius, at the wrist", () -> HandContacts.INSTANCE.wrist()),
        LUNATE("to the lunate", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.LUNATE)),
        TRAPEZIUM("to the trapezium",
                () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.TRAPEZIUM)),
        TRAPEZOID("to the trapezoid",
                () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.TRAPEZOID)),
        CAPITATE("to the capitate", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.CAPITATE));

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
        return "scaphoid";
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
        s.egg(v(1.65, -1.15, 0.25), 1.0 * light, 0.72, 0.62, 0, 0, 35);
    }
}

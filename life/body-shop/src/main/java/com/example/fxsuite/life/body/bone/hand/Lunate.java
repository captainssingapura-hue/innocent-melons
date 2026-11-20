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
 * The moon-shaped carpal in the middle of the first row, hollowed below for the capitate's head.
 */
public record Lunate() implements Bone {

    public static final Lunate INSTANCE = new Lunate();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Lunate> {
        RADIUS("on the radius, at the wrist", () -> HandContacts.INSTANCE.wrist()),
        SCAPHOID("to the scaphoid", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.LUNATE)),
        TRIQUETRUM("to the triquetrum",
                () -> HandContacts.INSTANCE.between(HandContacts.LUNATE, HandContacts.TRIQUETRUM)),
        CAPITATE("to the capitate", () -> HandContacts.INSTANCE.between(HandContacts.LUNATE, HandContacts.CAPITATE));

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
        return "lunate";
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
        s.egg(v(0.15, -0.95, 0.1), 0.72 * light, 0.66, 0.8);
        s.blend(0.3).carve().ball(v(0.0, -2.5, 0.0), 0.95);
    }
}

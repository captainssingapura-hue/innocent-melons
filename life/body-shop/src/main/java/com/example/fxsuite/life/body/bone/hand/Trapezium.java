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
 * The carpal of the second row under the thumb, saddled for its metacarpal, ridged on the palm's side.
 */
public record Trapezium() implements Bone {

    public static final Trapezium INSTANCE = new Trapezium();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Trapezium> {
        SCAPHOID("to the scaphoid", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.TRAPEZIUM)),
        TRAPEZOID("to the trapezoid",
                () -> HandContacts.INSTANCE.between(HandContacts.TRAPEZIUM, HandContacts.TRAPEZOID)),
        FIRST("its saddle, under the thumb's metacarpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.THUMB));

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
        return "trapezium";
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
        s.egg(v(2.2, -2.65, 0.6), 0.8 * light, 0.72, 0.68, 0, 25, 30);
        s.ball(v(2.3, -2.55, 1.2), 0.32);
    }
}

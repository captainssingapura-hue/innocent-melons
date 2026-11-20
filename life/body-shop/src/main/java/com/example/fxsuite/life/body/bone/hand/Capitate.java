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
 * The largest carpal, in the middle of the second row, its round head up in the lunate's hollow.
 */
public record Capitate() implements Bone {

    public static final Capitate INSTANCE = new Capitate();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Capitate> {
        SCAPHOID("to the scaphoid", () -> HandContacts.INSTANCE.between(HandContacts.SCAPHOID, HandContacts.CAPITATE)),
        LUNATE("to the lunate", () -> HandContacts.INSTANCE.between(HandContacts.LUNATE, HandContacts.CAPITATE)),
        TRAPEZOID("to the trapezoid",
                () -> HandContacts.INSTANCE.between(HandContacts.TRAPEZOID, HandContacts.CAPITATE)),
        HAMATE("to the hamate", () -> HandContacts.INSTANCE.between(HandContacts.CAPITATE, HandContacts.HAMATE)),
        THIRD("under the third metacarpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.MIDDLE));

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
        return "capitate";
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
        s.egg(v(-0.1, -2.75, -0.05), 0.72 * light, 1.1, 0.8);
        s.ball(v(-0.05, -1.85, -0.05), 0.62);
    }
}

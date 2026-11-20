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
 * The three-sided carpal of the first row, on the little finger's side.
 */
public record Triquetrum() implements Bone {

    public static final Triquetrum INSTANCE = new Triquetrum();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Triquetrum> {
        LUNATE("to the lunate", () -> HandContacts.INSTANCE.between(HandContacts.LUNATE, HandContacts.TRIQUETRUM)),
        PISIFORM("under the pisiform",
                () -> HandContacts.INSTANCE.between(HandContacts.TRIQUETRUM, HandContacts.PISIFORM)),
        HAMATE("to the hamate", () -> HandContacts.INSTANCE.between(HandContacts.TRIQUETRUM, HandContacts.HAMATE));

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
        return "triquetrum";
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
        s.egg(v(-1.25, -1.35, -0.1), 0.72 * light, 0.62, 0.6, 0, 0, -20);
    }
}

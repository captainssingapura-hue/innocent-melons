package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * An inferior nasal concha, one a side, a scroll of bone along the nose's cavity's outer wall, seen through its
 * opening.
 */
public record InferiorNasalConcha() implements Bone {

    public static final InferiorNasalConcha INSTANCE = new InferiorNasalConcha();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<InferiorNasalConcha> {
        MAXILLA("to the maxilla", () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.CONCHA));

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
        return "inferior nasal concha";
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
        return 0.05;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.3).egg(v(0.78, 1.75, 7.0), 0.28, 0.42, 1.85, 0, 0, -20);
        s.egg(v(0.62, 1.4, 7.0), 0.18, 0.3, 1.7);
    }
}

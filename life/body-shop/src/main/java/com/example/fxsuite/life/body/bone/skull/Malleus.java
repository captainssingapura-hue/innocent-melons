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
 * The hammer, one a side, the first of the middle ear's bones, its handle on the eardrum, behind the ear canal.
 */
public record Malleus() implements Bone {

    public static final Malleus INSTANCE = new Malleus();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Malleus> {
        INCUS("its head, on the anvil",
                () -> SkullContacts.INSTANCE.between(SkullContacts.MALLEUS, SkullContacts.INCUS));

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
        return "malleus";
    }

    @Override
    public Region region() {
        return Region.EAR;
    }

    @Override
    public Part part() {
        return Part.HEAD;
    }

    @Override
    public double coarsest() {
        return 0.02;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.04).rod(v(5.15, 1.25, -0.15), v(5.0, 1.95, -0.3), 0.07, 0.08);
        s.ball(v(4.95, 2.05, -0.35), 0.16);
    }
}

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
 * The stirrup, one a side, the smallest bone of the body, its footplate in the inner ear's window.
 */
public record Stapes() implements Bone {

    public static final Stapes INSTANCE = new Stapes();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Stapes> {
        INCUS("its head, on the anvil",
                () -> SkullContacts.INSTANCE.between(SkullContacts.INCUS, SkullContacts.STAPES)),
        WINDOW("its footplate, in the window",
                () -> SkullContacts.INSTANCE.between(SkullContacts.STAPES, SkullContacts.OVAL_WINDOW));

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
        return "stapes";
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
        s.blend(0.04).ring(v(4.68, 1.36, -0.52), v(1, 0, 0), 0.1, 0.03);
        s.egg(v(4.55, 1.36, -0.52), 0.03, 0.08, 0.14);
    }
}

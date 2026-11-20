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
 * The vomer, a thin blade down the middle of the nose's cavity, the back of its septum.
 */
public record Vomer() implements Bone {

    public static final Vomer INSTANCE = new Vomer();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Vomer> {
        SPHENOID("to the sphenoid", () -> SkullContacts.INSTANCE.between(SkullContacts.SPHENOID, SkullContacts.VOMER)),
        ETHMOID("to the ethmoid", () -> SkullContacts.INSTANCE.between(SkullContacts.ETHMOID, SkullContacts.VOMER)),
        MAXILLA("to the maxillae", () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.VOMER));

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
        return "vomer";
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
    public void draw(final Sketch s, final double female) {
        s.blend(0.2).plate(v(0, 0.5, 4.6), v(0, 3.6, 6.0), v(0, 0.6, 8.6), 0.2);
    }
}

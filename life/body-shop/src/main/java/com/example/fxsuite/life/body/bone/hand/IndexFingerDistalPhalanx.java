package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The distal phalanx of the index finger, in the hand's frame, on from the joint before it, bent a little.
 */
public record IndexFingerDistalPhalanx() implements Bone {

    public static final IndexFingerDistalPhalanx INSTANCE = new IndexFingerDistalPhalanx();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<IndexFingerDistalPhalanx> {
        BASE("its base, on the bone before it", () -> HandContacts.INSTANCE.interphalangeal(Digit.INDEX, 2));

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
        return "distal phalanx of the index finger";
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
        return 0.08;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Fingers.INSTANCE.phalanx(s, Digit.INDEX, 2, 1 - 0.12 * female);
    }
}

package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The fourth metacarpal, in the hand's frame: from its base in the wrist to its head, the knuckle.
 */
public record FourthMetacarpal() implements Bone {

    public static final FourthMetacarpal INSTANCE = new FourthMetacarpal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<FourthMetacarpal> {
        BASE("its base, on its carpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.RING)),
        HEAD("its head, under its finger", () -> HandContacts.INSTANCE.metacarpophalangeal(Digit.RING)),
        PREVIOUS("its base, beside the one toward the thumb",
                () -> HandContacts.INSTANCE.intermetacarpal(Digit.MIDDLE)),
        NEXT("its base, beside the one toward the little finger",
                () -> HandContacts.INSTANCE.intermetacarpal(Digit.RING));

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
        return "fourth metacarpal";
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
        Fingers.INSTANCE.metacarpal(s, Digit.RING, 1 - 0.12 * female);
    }
}

package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The third metacarpal, in the hand's frame: from its base in the wrist to its head, the knuckle.
 */
public record ThirdMetacarpal() implements Bone {

    public static final ThirdMetacarpal INSTANCE = new ThirdMetacarpal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<ThirdMetacarpal> {
        BASE("its base, on its carpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.MIDDLE)),
        HEAD("its head, under its finger", () -> HandContacts.INSTANCE.metacarpophalangeal(Digit.MIDDLE)),
        PREVIOUS("its base, beside the one toward the thumb", () -> HandContacts.INSTANCE.intermetacarpal(Digit.INDEX)),
        NEXT("its base, beside the one toward the little finger",
                () -> HandContacts.INSTANCE.intermetacarpal(Digit.MIDDLE));

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
        return "third metacarpal";
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
        Fingers.INSTANCE.metacarpal(s, Digit.MIDDLE, 1 - 0.12 * female);
    }
}

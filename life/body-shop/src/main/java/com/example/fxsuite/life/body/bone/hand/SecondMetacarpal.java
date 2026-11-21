package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The second metacarpal, in the hand's frame: from its base in the wrist to its head, the knuckle.
 */
public record SecondMetacarpal() implements Bone {

    public static final SecondMetacarpal INSTANCE = new SecondMetacarpal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<SecondMetacarpal> {
        BASE("its base, on its carpal", () -> HandContacts.INSTANCE.carpometacarpal(Digit.INDEX)),
        HEAD("its head, under its finger", () -> HandContacts.INSTANCE.metacarpophalangeal(Digit.INDEX)),
        NEXT("its base, beside the one toward the little finger",
                () -> HandContacts.INSTANCE.intermetacarpal(Digit.INDEX));

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
        return "second metacarpal";
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
        Fingers.INSTANCE.metacarpal(s, Digit.INDEX, 1 - 0.12 * female);
    }
}

package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The first metatarsal, in the foot's frame: from its base in the arch to its head the foot stands on.
 */
public record FirstMetatarsal() implements Bone {

    public static final FirstMetatarsal INSTANCE = new FirstMetatarsal();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<FirstMetatarsal> {
        BASE("its base, on its tarsal", () -> FootContacts.INSTANCE.tarsometatarsal(Toe.GREAT)),
        HEAD("its head, under its toe", () -> FootContacts.INSTANCE.metatarsophalangeal(Toe.GREAT));

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
        return "first metatarsal";
    }

    @Override
    public Region region() {
        return Region.FOOT;
    }

    @Override
    public Part part() {
        return Part.RIGHT_FOOT;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Toes.INSTANCE.metatarsal(s, Toe.GREAT, 1 - 0.08 * female);
    }
}

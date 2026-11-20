package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The proximal phalanx of the little toe, in the toes' frame, from the joint they turn at.
 */
public record LittleToeProximalPhalanx() implements Bone {

    public static final LittleToeProximalPhalanx INSTANCE = new LittleToeProximalPhalanx();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<LittleToeProximalPhalanx> {
        BASE("its base, on the bone before it", () -> FootContacts.INSTANCE.metatarsophalangealInToes(Toe.LITTLE)),
        HEAD("its head, under the bone after it", () -> FootContacts.INSTANCE.interphalangeal(Toe.LITTLE, 1));

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
        return "proximal phalanx of the little toe";
    }

    @Override
    public Region region() {
        return Region.FOOT;
    }

    @Override
    public Part part() {
        return Part.RIGHT_TOES;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Toes.INSTANCE.phalanx(s, Toe.LITTLE, 0);
    }
}

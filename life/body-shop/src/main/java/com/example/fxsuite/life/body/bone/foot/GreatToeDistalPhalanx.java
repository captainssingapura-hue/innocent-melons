package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The distal phalanx of the great toe, in the toes' frame, from the joint they turn at.
 */
public record GreatToeDistalPhalanx() implements Bone {

    public static final GreatToeDistalPhalanx INSTANCE = new GreatToeDistalPhalanx();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<GreatToeDistalPhalanx> {
        BASE("its base, on the bone before it", () -> FootContacts.INSTANCE.interphalangeal(Toe.GREAT, 1));

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
        return "distal phalanx of the great toe";
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
        Toes.INSTANCE.phalanx(s, Toe.GREAT, 1);
    }
}

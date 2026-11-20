package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The heel bone, in the foot's frame: its tuber behind and under, on a pad of flesh; its shelf for the talus inside;
 * forward to the cuboid; its upper face hollowed where the talus sits on it.
 */
public record Calcaneus() implements Bone {

    public static final Calcaneus INSTANCE = new Calcaneus();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Calcaneus> {
        TALUS("its top, under the talus", () -> FootContacts.INSTANCE.subtalar()),
        CUBOID("its front, on the cuboid", () -> FootContacts.INSTANCE.calcaneocuboid());

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
        return "calcaneus";
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
        return 0.25;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.08 * female;
        s.blend(0.9).egg(v(0.3, -3.7, -3.9), 1.5 * light, 1.6, 1.7);
        s.rod(v(0.3, -3.5, -3.0), v(1.25, -3.75, 2.3), 1.5 * light, 1.2 * light);
        s.blend(0.6).egg(v(-1.45, -2.85, 0.4), 0.9, 0.45, 1.1);
        s.ball(v(1.85, -4.35, 0.5), 0.35);
        s.blend(0.4).carve().egg(v(0.1, -1.55, -0.6), 1.3, 0.55, 1.6);
    }
}

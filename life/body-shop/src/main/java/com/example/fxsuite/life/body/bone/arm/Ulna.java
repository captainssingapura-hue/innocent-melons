package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The ulna, on the little finger's side of the forearm, in its frame from the elbow to the wrist 25 cm below, not
 * turning as the forearm twists: its olecranon behind the elbow, its notch round the trochlea, down to its head and its
 * styloid.
 */
public record Ulna() implements Bone {

    public static final Ulna INSTANCE = new Ulna();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Ulna> {
        TROCHLEA("its notch, round the trochlea", () -> ArmContacts.INSTANCE.trochlea(false)),
        RADIAL_NOTCH("its radial notch, round the radius's head", () -> ArmContacts.INSTANCE.radialNotch()),
        HEAD("its head, in the radius's notch", () -> ArmContacts.INSTANCE.ulnarNotch());

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
        return "ulna";
    }

    @Override
    public Region region() {
        return Region.ARM;
    }

    @Override
    public Part part() {
        return Part.RIGHT_FOREARM;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        s.blend(0.7);
        s.egg(v(-0.75, 1.0, -1.65), 1.05, 1.5, 1.1);
        s.egg(v(-0.7, -1.3, 1.05), 0.8, 0.75, 0.75);
        s.blend(0.3).carve().disc(v(-0.85, 0, 0.35), v(1, 0, 0), 1.3, 1.6, 0.2);
        s.blend(0.8).tube(1.05 * light, 0.62 * light, v(-0.8, -2.2, -0.5), v(-1.45, -12, -0.6), v(-1.85, -23.6, -0.5));
        s.blend(0.6).ball(v(-1.9, -24.2, -0.45), 0.82 * light);
        s.blend(0.4).rod(v(-2.2, -24.4, -0.85), v(-2.3, -25.2, -0.95), 0.4, 0.22);
    }
}

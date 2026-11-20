package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The upper arm bone, in the upper arm's frame from the shoulder joint to the elbow 31 cm below it: its head in the
 * glenoid, its tubercles and the groove between them, its shaft and the deltoid's tuberosity, its condyles at the
 * elbow.
 */
public record Humerus() implements Bone {

    public static final Humerus INSTANCE = new Humerus();

    @Override
    public String name() {
        return "humerus";
    }

    @Override
    public Region region() {
        return Region.ARM;
    }

    @Override
    public Part part() {
        return Part.RIGHT_UPPER_ARM;
    }

    @Override
    public double coarsest() {
        return 0.25;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        // the head, round the shoulder joint, facing in, up and back; the greater and lesser tubercles
        s.blend(0.6).ball(v(0, 0, 0), 2.35);
        s.blend(0.8).egg(v(1.85, 0.2, 0.15), 1.3, 1.7, 1.35);
        s.egg(v(0.7, -0.7, 1.65), 0.75, 1.0, 0.75);
        s.blend(0.35).carve().rod(v(1.2, -0.5, 1.85), v(1.0, -6.5, 1.25), 0.38, 0.32);
        // the surgical neck and the shaft, the deltoid's tuberosity at its side
        s.blend(0.9).tube(1.55 * light, 1.12 * light, v(0.9, -2.8, 0.05), v(0.4, -12, 0), v(0.05, -22, 0));
        s.blend(0.5).egg(v(1.18, -12, 0.25), 0.45, 2.6, 0.6);
        elbow(s, light);
    }

    /** Flattening and widening to the elbow: its epicondyles, the trochlea and the capitulum, its hollows. */
    private void elbow(final Sketch s, final double light) {
        s.blend(1.0).flat(v(0.05, -22, 0), v(0, -28.3, -0.15), 1.15 * light, 1.45 * light, v(0, 0, 1), 0.62);
        s.blend(1.2).plate(v(-2.8, -29.6, -0.35), v(2.45, -29.5, -0.1), v(0, -24.5, -0.1), 1.5);
        s.egg(v(-2.95, -29.6, -0.35), 0.9, 0.85, 0.75);
        s.egg(v(2.55, -29.5, -0.1), 0.65, 0.8, 0.65);
        s.blend(0.5).disc(v(-0.85, -31.0, 0.35), v(1, 0, 0), 1.2, 1.05, 0.35);
        s.blend(0.35).carve().ring(v(-0.85, -31.0, 0.35), v(1, 0, 0), 1.45, 0.4);
        s.blend(0.5).ball(v(1.35, -31.0, 0.7), 1.0);
        s.blend(0.4).carve().egg(v(-0.3, -29.3, -1.25), 1.05, 1.25, 0.65);
        s.carve().egg(v(-0.5, -29.6, 1.1), 0.7, 0.7, 0.4);
    }
}

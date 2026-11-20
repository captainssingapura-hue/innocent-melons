package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Moves;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The radius, on the thumb's side of the forearm, in its frame as it lies with the palm forward, turning across the
 * ulna as the forearm twists: its head under the capitulum, its neck and tuberosity, bowing out, broad at the wrist.
 */
public record Radius() implements Bone {

    public static final Radius INSTANCE = new Radius();

    @Override
    public String name() {
        return "radius";
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
    public Moves moves() {
        return Moves.RADIUS;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        s.blend(0.5).disc(v(1.45, -1.45, 0.55), v(0, 1, 0), 1.05, 0.45, 0.3);
        s.blend(0.3).carve().ball(v(1.35, 0.05, 0.7), 1.1);
        s.blend(0.6).rod(v(1.45, -1.9, 0.5), v(1.35, -3.6, 0.35), 0.62, 0.6);
        s.egg(v(0.95, -4.3, 0.65), 0.5, 0.7, 0.5);
        s.blend(0.8).tube(0.62 * light, 0.95 * light, v(1.35, -3.6, 0.35), v(2.1, -13, 0.3), v(2.0, -21, 0.3));
        s.blend(1.0).egg(v(1.55, -23.8, 0.35), 1.75 * light, 1.15, 1.05);
        s.blend(0.5).rod(v(2.9, -24.0, 0.25), v(3.05, -25.0, 0.2), 0.5, 0.25);
        s.blend(0.3).carve().ball(v(-0.6, -23.9, 0.25), 0.62);
    }
}

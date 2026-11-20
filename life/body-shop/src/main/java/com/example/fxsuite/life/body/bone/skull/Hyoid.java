package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The hyoid, in the neck's frame, under the jaw at the root of the tongue, held by muscle alone: its body across the
 * front, its greater horns back each side, its lesser horns up.
 */
public record Hyoid() implements Bone {

    public static final Hyoid INSTANCE = new Hyoid();

    @Override
    public String name() {
        return "hyoid";
    }

    @Override
    public Region region() {
        return Region.THROAT;
    }

    @Override
    public Part part() {
        return Part.NECK;
    }

    @Override
    public double coarsest() {
        return 0.08;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        s.blend(0.3).flat(v(-1.1, 4.2, 4.75), v(1.1, 4.2, 4.75), 0.42, 0.42, v(0, 0, 1), 0.55);
        for (final int side : new int[] {1, -1}) {
            s.tube(0.3, 0.2, v(side * 1.0, 4.25, 4.6), v(side * 2.0, 4.7, 3.4), v(side * 2.35, 5.05, 2.1));
            s.ball(v(side * 2.35, 5.05, 2.1), 0.22);
            s.rod(v(side * 0.9, 4.45, 4.7), v(side * 1.0, 4.95, 4.55), 0.14, 0.1);
        }
    }
}

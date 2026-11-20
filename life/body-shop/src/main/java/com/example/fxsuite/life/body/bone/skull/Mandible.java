package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The lower jaw, in the head's frame: its body round the front, tall and thin, its chin; its rami up from its angles to
 * the condyles in their hollows by the ears, the coronoid processes under the cheeks' arches; the arch its teeth sit
 * in. A woman's lighter, her chin and her jaw's angles less.
 */
public record Mandible() implements Bone {

    public static final Mandible INSTANCE = new Mandible();

    @Override
    public String name() {
        return "mandible";
    }

    @Override
    public Region region() {
        return Region.SKULL;
    }

    @Override
    public Part part() {
        return Part.HEAD;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double strong = 1 - 0.3 * female;
        for (final int side : new int[] {1, -1}) {
            half(s, side, strong);
        }
        s.blend(0.6).egg(v(0, -4.9, 8.3), 1.3 * strong, 0.85, 0.5);
        s.ball(v(1.15, -5.1, 8.1), 0.4 * strong);
        s.ball(v(-1.15, -5.1, 8.1), 0.4 * strong);
    }

    /** One side: its body to the chin, its ramus, condyle and coronoid, its angle, the arch for its teeth. */
    private void half(final Sketch s, final int side, final double strong) {
        final V3[] body = {v(side * 4.85, -3.5, -0.3), v(side * 4.3, -4.2, 2.6), v(side * 3.0, -4.35, 5.6),
            v(side * 1.4, -4.15, 7.6), v(0, -4.1, 8.25)};
        for (int k = 0; k + 1 < body.length; k++) {
            final V3 a = body[k];
            final V3 b = body[k + 1];
            s.blend(0.8).flat(a, b, 1.3, 1.3, v(a.x() + b.x(), 0, a.z() + b.z() - 2.0).unit(), 0.38);
        }
        s.blend(0.4).plate(v(side * 4.95, -3.7, -0.7), v(side * 4.65, -2.8, 2.4), v(side * 4.8, 0.3, 2.2),
                v(side * 5.1, 0.5, -0.4), 0.55);
        s.blend(0.5).egg(v(side * 5.15, 1.0, -0.15), 1.0, 0.45, 0.5, 0, side * 15, 0);
        s.rod(v(side * 5.05, 0.4, -0.3), v(side * 5.1, 0.85, -0.2), 0.4, 0.45);
        s.rod(v(side * 4.75, 0.0, 2.0), v(side * 4.85, 1.85, 2.35), 0.42, 0.22);
        s.ball(v(side * 4.95, -3.45, -0.45), 0.7 * strong);
        s.blend(0.6).tube(0.68, 0.62, v(side * 2.9, -2.75, 3.8), v(side * 2.45, -2.75, 6.1),
                v(side * 1.55, -2.75, 7.6), v(0, -2.75, 8.15));
        s.blend(0.3).carve().ball(v(side * 2.4, -4.0, 7.05), 0.24);
    }
}

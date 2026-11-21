package com.example.fxsuite.life.body.bone.hip;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * A hip bone, its ilium, ischium and pubis grown into one round the hip joint, drawn in the pelvis's frame (x to the
 * right, y up from the hip joints, z ahead), one a side: the acetabulum cupped round the head of the femur 8.5 cm out;
 * the ilium's wing up and out to its crest, from the anterior superior spine to the posterior where it meets the
 * sacrum; the pubis forward and in to the symphysis; the ischium down and back to the tuberosity sat on; the obturator
 * foramen open between them. A woman's wider, its wing flaring further, its arch under the symphysis wider, its sitting
 * bones further apart.
 */
public record HipBone() implements Bone {

    public static final HipBone INSTANCE = new HipBone();

    /** The hip joint, the head of the femur's middle; and the way the acetabulum faces, out, down and forward. */
    public static final V3 JOINT = v(8.5, 0, 0);
    public static final V3 FACES = v(0.75, -0.45, 0.5).unit();

    /** Where it meets its neighbours, as on the right. */
    public enum Points implements Point<HipBone> {
        SACRUM("on the sacrum", () -> PelvisContacts.INSTANCE.sacroiliac()),
        SYMPHYSIS("on its fellow, in front", () -> PelvisContacts.INSTANCE.symphysis()),
        ACETABULUM("round the head of the femur", () -> PelvisContacts.INSTANCE.hip());

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
        return "hip bone";
    }

    @Override
    public Region region() {
        return Region.HIP;
    }

    @Override
    public Part part() {
        return Part.PELVIS;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final double light = 1 - 0.12 * female;
        ilium(s, female, light);
        acetabulum(s);
        ischiumAndPubis(s, female, light);
        // its face at the symphysis, flat to its fellow's
        s.blend(0.2).clip().plane(v(0.5, -4.4 + 0.4 * female, 6.0), v(-1, 0, 0));
    }

    /** The ilium: its wing, a curved blade from its crest to the acetabulum and the notch; its spines; its brim. */
    private void ilium(final Sketch s, final double f, final double light) {
        final V3 asis = v(12.0 + 1.2 * f, 7.0 - 0.6 * f, 5.5);
        final V3 aiis = v(10.4 + 0.5 * f, 3.4, 4.3);
        final V3 tubercle = v(14.2 + 0.9 * f, 11.0 - 0.8 * f, 1.2);
        final V3 crestBack = v(12.6 + 0.6 * f, 13.4 - 1.0 * f, -4.0);
        final V3 psis = v(4.6, 9.5 - 0.5 * f, -8.0);
        final V3 piis = v(4.8, 6.5 - 0.4 * f, -8.0);
        final V3 notch = v(6.4 + 0.5 * f, 2.6, -5.8);
        final V3 fossa = v(10.6 + 0.8 * f, 7.6 - 0.5 * f, -0.8);
        // the wing, hollowed inside as the iliac fossa, bulging out where the gluteal muscles lie on it
        s.blend(0.5).sheet(new V3[] {asis, tubercle, crestBack, psis}, new V3[] {aiis, v(9.3, 2.6, 0.2), notch, piis},
                8, 4, 0.55 * light, v(1, 0, -0.2), 0.9);
        s.blend(0.9).egg(fossa.plus(v(-1.6, -3.6, 0.4)), 1.6, 1.8, 2.2);
        // its crest, thick, from spine to spine; the spines themselves
        s.blend(0.6).tube(0.72 * light, 0.62 * light, asis, tubercle, crestBack, psis);
        s.ball(asis, 0.62);
        s.ball(aiis, 0.55);
        s.ball(psis, 0.62);
        s.ball(piis, 0.5);
        // where it meets the sacrum, thick, and the brim of the pelvis round to the pubis
        s.blend(0.9).egg(v(5.3, 6.2 - 0.3 * f, -6.6), 1.0, 2.4, 1.6);
        s.blend(0.6).tube(0.55, 0.5, v(5.1, 5.0, -5.6), v(6.3 + 0.6 * f, 1.6, -1.6), v(5.6 + 0.4 * f, -0.6, 2.6),
                v(2.6, -2.8, 5.5));
    }

    /** The acetabulum: thick bone cupped round the femur's head, its rim, the notch in its rim below. */
    private void acetabulum(final Sketch s) {
        s.blend(0.9).egg(JOINT.minus(FACES.times(1.0)), FACES, v(0, 1, 0), 1.6, 3.3, 3.2);
        s.blend(0.5).ring(JOINT.plus(FACES.times(0.35)), FACES, 2.75, 0.5);
        s.blend(0.3).carve().ball(JOINT, 2.55);
        s.carve().ball(JOINT.plus(v(-0.2, -2.6, -0.1)), 0.9);
    }

    /** The ischium, down and back to the tuberosity, its spine behind; the pubis in to the symphysis and back. */
    private void ischiumAndPubis(final Sketch s, final double f, final double light) {
        final V3 spine = v(5.2 + 0.8 * f, -2.8, -4.6);
        final V3 tuberosity = v(5.6 + 1.0 * f, -8.4 + 0.6 * f, -2.6);
        final V3 rami = v(3.6 + 1.2 * f, -8.4 + 0.8 * f, 2.4);
        s.blend(0.8).rod(JOINT.plus(v(-0.6, -2.2, -1.2)), tuberosity, 1.35 * light, 1.15 * light);
        s.egg(tuberosity, 1.2, 1.6, 1.4);
        s.blend(0.5).rod(v(6.6, -1.6, -3.8), spine, 0.62, 0.3);
        s.blend(0.7).rod(JOINT.plus(v(-1.5, -0.6, 2.4)), v(2.4, -3.4, 5.6), 0.9 * light, 0.85 * light);
        s.egg(v(1.8, -4.6 + 0.4 * f, 5.6), 1.2, 1.6, 0.85);
        s.ball(v(2.6, -3.6 + 0.3 * f, 6.5), 0.45);
        s.blend(0.6).tube(0.75 * light, 0.65 * light, v(1.4, -5.6 + 0.5 * f, 5.4), rami,
                tuberosity.plus(v(0, -0.4, 0.6)));
    }
}

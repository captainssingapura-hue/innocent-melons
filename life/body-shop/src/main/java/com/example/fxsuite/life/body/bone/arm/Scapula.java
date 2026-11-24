package com.example.fxsuite.life.body.bone.arm;

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
 * The shoulder blade, drawn in the body's frame at rest and moving with the shoulder girdle: a thin blade curved to lie
 * on the back of the chest, thick along its lateral border; its spine across its back rising to the acromion over the
 * shoulder joint; its glenoid hollowed round the head of the humerus; its coracoid hooked forward under the clavicle.
 */
public record Scapula() implements Bone {

    public static final Scapula INSTANCE = new Scapula();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Scapula> {
        CLAVICLE("its acromion, under the clavicle's end", () -> ArmContacts.INSTANCE.acromioclavicular()),
        GLENOID("its glenoid, round the humerus's head", () -> ArmContacts.INSTANCE.glenoid());

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

    /** The shoulder joint, in the body's frame at rest: where the head of the humerus turns. */
    public static final V3 JOINT = v(18, 44.5, 0);
    /** The way the glenoid faces: out and forward. */
    public static final V3 FACING = v(0.82, 0, 0.57);

    @Override
    public String name() {
        return "scapula";
    }

    @Override
    public Region region() {
        return Region.SHOULDER;
    }

    @Override
    public Part part() {
        return Part.RIGHT_GIRDLE;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    /**
     * Drawn as a form of its landmarks fitted to adult measures ({@link ScapulaForm}) where {@code -Dbones.scapula=form}
     * asks; else as it was first sketched.
     */
    @Override
    public void draw(final Sketch s, final double female) {
        if ("form".equals(System.getProperty("bones.scapula"))) {
            final ScapulaForm form = new ScapulaForm();
            form.bone().drawn(s, form.placed());
            return;
        }
        final double light = 1 - 0.12 * female;
        blade(s, light);
        spine(s);
        glenoid(s);
        // the coracoid, up and hooked forward
        s.blend(0.5).tube(0.8, 0.55, v(13.4, 46.1, -3.9), v(14.2, 46.5, -0.8), v(14.6, 44.9, 3.6));
    }

    /** The blade, thin, curved to lie on the chest, from its medial border to its lateral; and its borders. */
    private void blade(final Sketch s, final double light) {
        final V3 superior = v(7.2, 47.8, -10.0);
        final V3 inferior = v(9.3, 34.0, -9.2);
        final V3 root = v(6.6, 43.8, -10.3);
        final V3 lateral = v(12.2, 38.3, -5.5);
        final V3 neck = v(14.9, 44.1, -2.4);
        final V3 notch = v(12.2, 46.6, -5.2);
        s.blend(0.3);
        s.sheet(new V3[] {superior, root, inferior}, new V3[] {notch, neck, lateral, inferior}, 7, 4, 0.34,
                v(-0.3, 0, -1), 0.45);
        s.blend(0.4);
        s.tube(0.6 * light, 0.45 * light, neck, lateral, inferior);
        s.tube(0.32, 0.4, superior, root, inferior);
        s.tube(0.3, 0.35, superior, notch);
        s.rod(notch, v(13.4, 46.1, -3.9), 0.45, 0.6);
    }

    /** Its spine, standing off the blade's back and rising out to the acromion, a flat roof over the joint. */
    private void spine(final Sketch s) {
        s.blend(0.35);
        s.plate(v(6.6, 43.8, -10.3), v(14.2, 45.6, -5.0), v(14.0, 46.6, -6.7), v(6.9, 44.1, -11.3), 0.36);
        s.tube(0.38, 0.55, v(6.9, 44.1, -11.3), v(14.0, 46.6, -6.7));
        s.blend(0.5);
        s.plate(v(14.0, 46.8, -6.6), v(16.6, 47.55, 0.9), v(19.0, 47.3, -0.3), v(18.1, 47.2, -3.7), 0.62);
    }

    /** The glenoid: its rim round the humerus's head, its tubercles above and below, hollowed to fit the head. */
    private void glenoid(final Sketch s) {
        final V3 g = JOINT.minus(FACING.times(2.75));
        s.blend(0.6).egg(g, FACING, v(0, 1, 0), 0.75, 1.95, 1.35);
        s.ball(g.plus(v(0, 2.0, -0.1)), 0.42);
        s.ball(g.plus(v(-0.2, -2.2, -0.2)), 0.5);
        s.blend(0.25).carve().ball(JOINT, 2.5);
    }
}

package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.bone.hip.PelvisContacts;

import java.util.function.Supplier;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The sacrum, five vertebrae grown into one, drawn in the body's frame at rest and moving with the pelvis: broad at its
 * top between the ilia, narrowing down and back to its apex, hollowed in front, its foramina in rows down its front,
 * its crest down its back. A woman's broader.
 */
public record Sacrum() implements Bone {

    public static final Sacrum INSTANCE = new Sacrum();

    @Override
    public String name() {
        return "sacrum";
    }

    @Override
    public Region region() {
        return Region.SPINE;
    }

    @Override
    public Part part() {
        return Part.PELVIS;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    /** Where it meets its neighbours: the fifth lumbar on the disc above it, the coccyx at its apex, each ilium. */
    public enum Points implements Point<Sacrum> {
        ABOVE("on the disc under the fifth lumbar",
                () -> Discs.INSTANCE.inPart(Discs.INSTANCE.below(Level.L5), Part.PELVIS)),
        BELOW("its apex, on the coccyx", () -> Discs.INSTANCE.inPart(Discs.INSTANCE.sacrococcygeal(), Part.PELVIS)),
        ILIUM("its wing, on the ilium, as on the right", () -> PelvisContacts.INSTANCE.sacroiliac());

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
    public void draw(final Sketch s, final double female) {
        final double broad = 1 + 0.08 * female;
        final double light = 1 - 0.1 * female;
        final V3[] spine = {v(0, 9.45, -3.5), v(0, 7.3, -5.15), v(0, 5.3, -6.2), v(0, 3.45, -6.85), v(0, 1.85, -7.1)};
        final double[] wide = {2.45, 2.0, 1.55, 1.15, 0.8};
        final double[] deep = {1.55, 1.25, 1.0, 0.8, 0.6};
        // the first's body, its top tilted forward to meet the fifth lumbar; the rest down its curve
        s.blend(0.7).disc(spine[0], v(0, 0.77, 0.64), wide[0] * broad, 0.95, 0.3, v(0, -0.64, 0.77), 0.66);
        for (int k = 1; k < spine.length; k++) {
            final V3 up = spine[k - 1].minus(spine[k]).unit();
            s.disc(spine[k], up, wide[k] * broad, 0.8, 0.3, V3.X.cross(up).unit(), deep[k] / wide[k]);
        }
        for (final int side : new int[] {1, -1}) {
            wing(s, side, broad, light);
        }
        // its crest down its back, its canal opening at the top
        s.blend(0.5).tube(0.42, 0.32, v(0, 9.1, -5.7), v(0, 6.2, -7.3), v(0, 3.4, -8.0));
        s.blend(0.3).carve().tube(0.62, 0.32, v(0, 10.2, -4.9), v(0, 6.5, -6.6), v(0, 2.8, -7.4));
    }

    /** Its wing out to the ilium on one side, its lateral part down that side, its foramina in a row. */
    private void wing(final Sketch s, final int side, final double broad, final double light) {
        s.blend(0.9);
        s.plate(v(side * 1.2, 10.1, -3.4), v(side * 5.0 * broad, 9.3, -4.6), v(side * 4.6 * broad, 5.9, -6.6),
                v(side * 1.4, 6.6, -5.6), 1.7 * light);
        s.plate(v(side * 1.4, 6.6, -5.6), v(side * 4.6 * broad, 5.9, -6.6), v(side * 1.5, 2.2, -7.3), 1.2 * light);
        s.blend(0.3);
        for (final V3 hole : new V3[] {v(1.45, 8.0, -3.1), v(1.45, 6.0, -4.4), v(1.45, 4.15, -5.35),
            v(1.45, 2.6, -5.95)}) {
            s.carve().ball(v(side * hole.x(), hole.y(), hole.z()), 0.42);
        }
    }
}

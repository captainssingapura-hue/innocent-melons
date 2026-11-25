package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;

/**
 * A head made elsewhere, read from a file on the machine, never kept in the code ({@link ReferenceReading}): its
 * surface in our head's frame (its x to the right, y up, z ahead, cm), where it was read from, its eyes and the middle
 * of its mouth.
 */
public record ReferenceHead(Mesher.Mesh mesh, String from, Eye rightEye, Eye leftEye, V3 mouth) {

    /** An eye: its eyeball's middle, its upper lid's and its lower lid's, how far round its eyeball is, cm. */
    public record Eye(V3 middle, V3 upperLid, V3 lowerLid, double radius) {

        /** It moved as {@code w} sizes its head, its eyeball as big again as the face round it. */
        public Eye warped(final HeadWarp w) {
            return new Eye(w.apply(middle), w.apply(upperLid), w.apply(lowerLid), radius * w.round());
        }
    }

    /** The head made a man's size region by region as {@code w} makes it: its surface, its eyes and its mouth. */
    public ReferenceHead warped(final HeadWarp w) {
        final float[] p = mesh.points().clone();
        for (int i = 0; i < p.length; i += 3) {
            final V3 q = w.apply(new V3(p[i], p[i + 1], p[i + 2]));
            p[i] = (float) q.x();
            p[i + 1] = (float) q.y();
            p[i + 2] = (float) q.z();
        }
        return new ReferenceHead(new Mesher.Mesh(p, mesh.triangles()), from, rightEye.warped(w), leftEye.warped(w),
                w.apply(mouth));
    }
}

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

        /** It moved as a head {@code k} times as big along each way, from its middle; its eyeball as round. */
        public Eye scaled(final V3 k) {
            return new Eye(middle.times(k), upperLid.times(k), lowerLid.times(k), radius);
        }
    }

    /**
     * The head made {@code k} times as big along each way, from its middle, the head's joint: its surface, its eyes'
     * places and its mouth's; its eyeballs as round as they were, a grown man's being much of a size.
     */
    public ReferenceHead scaled(final V3 k) {
        final float[] p = mesh.points().clone();
        for (int i = 0; i < p.length; i += 3) {
            p[i] *= (float) k.x();
            p[i + 1] *= (float) k.y();
            p[i + 2] *= (float) k.z();
        }
        return new ReferenceHead(new Mesher.Mesh(p, mesh.triangles()), from, rightEye.scaled(k), leftEye.scaled(k),
                mouth.times(k));
    }
}

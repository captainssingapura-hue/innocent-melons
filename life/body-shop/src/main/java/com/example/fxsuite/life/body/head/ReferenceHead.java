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
    }
}

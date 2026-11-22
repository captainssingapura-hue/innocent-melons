package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;

/**
 * A head made elsewhere, read from a file on the machine, never kept in the code ({@link ReferenceReading}): its
 * surface in our head's frame (its x to the right, y up, z ahead, cm), where it was read from, and its landmarks, the
 * middles of its eyes and of its mouth.
 */
public record ReferenceHead(Mesher.Mesh mesh, String from, V3 rightEye, V3 leftEye, V3 mouth) {
}

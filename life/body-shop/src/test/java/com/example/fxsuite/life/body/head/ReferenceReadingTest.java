package com.example.fxsuite.life.body.head;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A reference head read from a body's mesh: of its skin, only what is above its neck; at its own size, its decimetres
 * our centimetres, its head's joint our head's middle, its right made ours; its eyes and its mouth with it.
 */
class ReferenceReadingTest {

    @TempDir
    private Path dir;

    @Test
    void itsSkinAboveItsNeckAtItsOwnSize() throws IOException {
        final Path obj = dir.resolve("body.obj");
        Files.writeString(obj, String.join("\n",
                "v 0 1.0 0", "v -0.2 0.5 0.3", "v 0.2 0.5 0.3", "v 0 -1 0", "v 0 -0.4 0",
                "v -0.3 0.5 0.5", "v 0.3 0.5 0.5", "v 0 0 0", "v 0 0.2 0.6",
                "g body", "f 1 2 3", "f 2 3 4",
                "g joint-neck", "f 5 5 5",
                "g joint-head", "f 8 8 8",
                "g joint-r-eye", "f 6 6 6",
                "g joint-l-eye", "f 7 7 7",
                "g joint-mouth", "f 9 9 9", ""));
        final ReferenceHead r = ReferenceReading.INSTANCE.of(obj);
        assertEquals(1, r.mesh().triangleCount(), "only the face above the neck");
        final float[] p = r.mesh().points();
        assertEquals(10, p[1], 1e-4, "its top a decimetre above its head's joint, ten centimetres");
        assertEquals(2, p[3], 1e-4, "its point on its right, at its x's less, on ours, at our x's more");
        assertEquals(3, r.rightEye().x(), 1e-9, "its right eye on our right");
        assertEquals(6, r.mouth().z(), 1e-9, "its mouth as far ahead as it was, in centimetres");
    }
}

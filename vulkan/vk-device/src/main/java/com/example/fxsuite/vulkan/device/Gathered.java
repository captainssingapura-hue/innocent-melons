package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * The things of a frame as they are gathered to be kept for rays: each a mesh and where it stands, taken from what
 * the thing is told when it is drawn. Written out, each is as Vulkan reads one piece of the frame: where it stands,
 * row by row; that any ray may meet it, from either side; and where on the card its mesh's triangles are kept.
 */
final class Gathered {

    /** How many bytes one thing is written in. */
    static final int EACH = 64;
    private static final int ROWS = 3;
    private static final int ACROSS = 4;
    /** Met by every ray, in the last byte of its fourth number. */
    private static final int ANY_RAY = 0xff << 24;
    /** Met from either side: Vulkan's own flag for it, in the last byte of its fifth number. */
    private static final int EITHER_SIDE = 1 << 24;

    private record Stood(long kept, float[] placed) {
    }

    private final List<Stood> all = new ArrayList<>();

    /** {@code mesh} added, standing where {@code placed} says, read as a look laid out by {@code laid} is told. */
    void stood(final Laid laid, final Sent mesh, final Filling placed) {
        if (!placed.layout().equals(laid.draw())) {
            throw new IllegalArgumentException("a thing is told what is laid out as " + laid.draw() + ", not as "
                    + placed.layout());
        }
        final Structured solid = mesh.solid().orElseThrow(() -> new IllegalStateException(
                "the mesh was not kept for rays to be asked of"));
        final ByteBuffer told = placed.filled();
        final int at = placed.layout().blockAt(laid.stands());
        final float[] rows = new float[ROWS * ACROSS];
        for (int row = 0; row < ROWS; row++) {
            for (int across = 0; across < ACROSS; across++) {
                // told a column at a time, four numbers to a column; written a row at a time
                rows[row * ACROSS + across] = told.getFloat(at + Float.BYTES * (across * ACROSS + row));
            }
        }
        all.add(new Stood(solid.address(), rows));
    }

    /** How many things were gathered. */
    int count() {
        return all.size();
    }

    /** All of them, written out, in room for {@code most}: what is left over is nothing. */
    ByteBuffer written(final int most) {
        final ByteBuffer bytes = ByteBuffer.allocate(EACH * most).order(ByteOrder.LITTLE_ENDIAN);
        for (final Stood one : all) {
            for (final float number : one.placed()) {
                bytes.putFloat(number);
            }
            bytes.putInt(ANY_RAY).putInt(EITHER_SIDE).putLong(one.kept());
        }
        return bytes.rewind();
    }
}

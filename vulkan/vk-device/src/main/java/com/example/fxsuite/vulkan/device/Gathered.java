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

    /**
     * {@code mesh} added once for each of {@code copies}, each standing where {@code placed} says the whole does,
     * after its copy has taken it there: read as a look laid out by {@code laid} is told.
     */
    void stood(final Laid laid, final Sent mesh, final Filling placed, final List<Filling> copies) {
        if (!placed.layout().equals(laid.draw())) {
            throw new IllegalArgumentException("a thing is told what is laid out as " + laid.draw() + ", not as "
                    + placed.layout());
        }
        final Structured solid = mesh.solid().orElseThrow(() -> new IllegalStateException(
                "the mesh was not kept for rays to be asked of"));
        final double[] whole = matrix(placed.filled(), placed.layout().blockAt(laid.stands()));
        for (final Filling copy : copies) {
            final double[] each = matrix(copy.filled(), 0);
            final float[] rows = new float[ROWS * ACROSS];
            for (int row = 0; row < ROWS; row++) {
                for (int across = 0; across < ACROSS; across++) {
                    double sum = 0;
                    for (int k = 0; k < ACROSS; k++) {
                        sum += whole[row * ACROSS + k] * each[k * ACROSS + across];
                    }
                    rows[row * ACROSS + across] = (float) sum;
                }
            }
            all.add(new Stood(solid.address(), rows));
        }
    }

    /** The matrix told from {@code at} on in {@code told}, a column at a time: as rows, four numbers to a row. */
    private double[] matrix(final ByteBuffer told, final int at) {
        final double[] rows = new double[ACROSS * ACROSS];
        for (int row = 0; row < ACROSS; row++) {
            for (int across = 0; across < ACROSS; across++) {
                rows[row * ACROSS + across] = told.getFloat(at + Float.BYTES * (across * ACROSS + row));
            }
        }
        return rows;
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

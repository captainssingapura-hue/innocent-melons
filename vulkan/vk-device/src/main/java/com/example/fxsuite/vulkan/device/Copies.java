package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.layout.Layout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

/**
 * Copies of whatever is drawn with them, kept on the card: each told what takes a mesh from its own frame to where
 * the copy stands, as {@code copy} lays one out. One draw makes every copy ({@link Pass#draw(Drawing, Sent, Filling,
 * Copies)}), in the order they were filled, so what is seen through is filled the furthest first. Filled afresh as
 * often as wanted, more room made when there are more than there was room for; but filled at most once for each
 * picture drawn, and before anything is drawn with it in that picture, since the card reads them only when the
 * picture is drawn. Closed, they are gone from the card.
 */
public final class Copies implements AutoCloseable {

    private final Reach reach;
    private final Layout copy;
    private Bytes bytes;
    private int most;
    private List<Filling> each = List.of();

    /** Room for {@code most} copies, laid out as {@code copy}, none of them filled yet. */
    Copies(final Reach reach, final Layout copy, final int most) {
        if (copy.packedBytes() != copy.blockBytes()) {
            throw new IllegalArgumentException("a copy is laid out packed as a block is: " + copy);
        }
        this.reach = reach;
        this.copy = copy;
        roomFor(Math.max(1, most));
    }

    private void roomFor(final int many) {
        this.most = many;
        this.bytes = new Bytes(reach, "copies of what is drawn", copy.packedBytes() * many,
                VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
    }

    /** Filled with {@code copies}, in their order, each laid out as a copy is; any laid out otherwise is refused. */
    public Copies filled(final List<Filling> copies) {
        for (final Filling one : copies) {
            if (!one.layout().equals(copy)) {
                throw new IllegalArgumentException("copies are laid out as " + copy + ", not as " + one.layout());
            }
        }
        if (copies.size() > most) {
            bytes.close();
            roomFor(Math.max(copies.size(), 2 * most));
        }
        final ByteBuffer all = ByteBuffer.allocate(copy.packedBytes() * most).order(ByteOrder.LITTLE_ENDIAN);
        // a copy's one field is a block and packed alike: its bytes as they are
        copies.forEach(one -> all.put(one.filled()));
        bytes.filled(all.rewind());
        this.each = List.copyOf(copies);
        return this;
    }

    /** How many copies it holds. */
    public int count() {
        return each.size();
    }

    /** Each copy as it was filled: for what keeps the things of a frame for rays. */
    List<Filling> each() {
        return each;
    }

    Bytes bytes() {
        return bytes;
    }

    @Override
    public void close() {
        bytes.close();
    }
}

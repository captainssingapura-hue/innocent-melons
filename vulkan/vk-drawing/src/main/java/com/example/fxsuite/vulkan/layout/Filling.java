package com.example.fxsuite.vulkan.layout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

/**
 * A block's bytes being filled, a field at a time by name, each set where the layout says it goes. A field the layout
 * has not, a field given the wrong number of numbers, and a block handed over with a field never set are each
 * refused: what reaches the card is whole, and where the shader will look for it.
 */
public final class Filling {

    private final Layout layout;
    private final ByteBuffer bytes;
    private final Set<String> set = new HashSet<>();

    /** {@code layout}'s block, nothing of it set yet. */
    public Filling(final Layout layout) {
        this.layout = layout;
        this.bytes = ByteBuffer.allocate(layout.blockBytes()).order(ByteOrder.LITTLE_ENDIAN);
    }

    /** The layout whose block this is. */
    public Layout layout() {
        return layout;
    }

    /** The field called {@code name} set to {@code numbers}, each made single; this, to set the next. */
    public Filling with(final String name, final double... numbers) {
        final Field field = layout.field(name);
        if (numbers.length != field.kind().numbers()) {
            throw new IllegalArgumentException(layout.name() + "." + name + " is " + field.kind().numbers()
                    + " numbers, not " + numbers.length);
        }
        final int at = layout.blockAt(name);
        for (int k = 0; k < numbers.length; k++) {
            bytes.putFloat(at + Float.BYTES * k, (float) numbers[k]);
        }
        set.add(name);
        return this;
    }

    /** The block, whole: its bytes, from the first, not to be written to. */
    public ByteBuffer filled() {
        for (final Field field : layout.fields()) {
            if (!set.contains(field.name())) {
                throw new IllegalStateException(layout.name() + "." + field.name() + " was never set");
            }
        }
        return bytes.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN).rewind();
    }
}

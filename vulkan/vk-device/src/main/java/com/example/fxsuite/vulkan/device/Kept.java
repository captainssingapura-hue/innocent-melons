package com.example.fxsuite.vulkan.device;

import java.util.function.LongConsumer;

/**
 * One of the card's things, held: its handle, which is a bare number, kept in here and marked with the kind of thing
 * it is. The number is given out only to make a call with, only to the owner's thread, and only while the thing is
 * open; once closed it is refused, by name. Closing it twice does nothing the second time.
 */
final class Kept<K extends Of> {

    private final String name;
    private final long raw;
    private final LongConsumer closing;
    private final Owner owner;
    private boolean closed;

    Kept(final String name, final long raw, final LongConsumer closing, final Owner owner) {
        this.name = name;
        this.raw = raw;
        this.closing = closing;
        this.owner = owner;
    }

    String name() {
        return name;
    }

    /** The handle, to make a call with. */
    long raw() {
        owner.mine();
        if (closed) {
            throw new IllegalStateException(name + " is closed, and cannot be used");
        }
        return raw;
    }

    void close() {
        owner.mine();
        if (!closed) {
            closed = true;
            closing.accept(raw);
            owner.gone(this);
        }
    }
}

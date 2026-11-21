package com.example.fxsuite.vulkan.device;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * What holds the card's things while they live. Each is kept here as it is made, in order, and gone from here as it
 * is closed; what is left at the end is closed newest first, and named. The thread that made the owner is the one
 * thread that may use anything it holds: asked of every use, and any other thread is refused.
 */
final class Owner {

    private final Thread thread = Thread.currentThread();
    private final List<Kept<?>> alive = new ArrayList<>();

    /** Refuses any thread but the owner's own. */
    void mine() {
        if (Thread.currentThread() != thread) {
            throw new IllegalStateException("the card is used by the thread " + thread.getName()
                    + " alone, and was called from " + Thread.currentThread().getName());
        }
    }

    /** {@code raw}, held from now on as {@code name}, to be closed by {@code closing}. */
    <K extends Of> Kept<K> kept(final String name, final long raw, final LongConsumer closing) {
        mine();
        final Kept<K> kept = new Kept<>(name, raw, closing, this);
        alive.add(kept);
        return kept;
    }

    void gone(final Kept<?> kept) {
        alive.remove(kept);
    }

    /** How many things are alive now. */
    int alive() {
        return alive.size();
    }

    /** Whatever is still alive closed, newest first; their names, in the order they were closed. */
    List<String> swept() {
        return unmade(0);
    }

    /**
     * The newest things closed, newest first, until no more than {@code left} are alive: what was made since there
     * were {@code left}, unmade. Their names, in the order they were closed.
     */
    List<String> unmade(final int left) {
        mine();
        final List<String> names = new ArrayList<>();
        while (alive.size() > left) {
            final Kept<?> newest = alive.getLast();
            names.add(newest.name());
            newest.close();
        }
        return names;
    }
}

package com.example.fxsuite.vulkan.bench;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the bench's controls ask of whoever draws. The controls are JavaFX's and live on its thread; the card is
 * used by one thread of its own, and the way the subject is looked at is that thread's too, since the mouse moves
 * it in the card's window. Nothing of the card is touched from JavaFX's thread: what is wanted is left here, each
 * thing whole and never changed once said, and the card's thread asks as each frame begins.
 */
public final class Asked {

    private final AtomicBoolean stopped = new AtomicBoolean();
    private final AtomicBoolean first = new AtomicBoolean();
    private final AtomicReference<Double> further;

    /** Asking, to begin with, for the first of the row to be shown as from {@code further} times as far. */
    public Asked(final double further) {
        this.further = new AtomicReference<>(further);
    }

    /** Asks for the first of the row to be shown as from {@code times} times as far, from the next frame on. */
    public void further(final double times) {
        further.set(times);
    }

    /** How many times as far the first of the row was last asked to be shown as from. */
    public double further() {
        return further.get();
    }

    /** Asks for the subject to be looked at again as it was to begin with. */
    public void first() {
        first.set(true);
    }

    /** Whether the first view has been asked for since this was last asked; once answered, it is not wanted again. */
    public boolean firstWanted() {
        return first.getAndSet(false);
    }

    /** Asks for the bench to stop. */
    public void stop() {
        stopped.set(true);
    }

    public boolean stopped() {
        return stopped.get();
    }
}

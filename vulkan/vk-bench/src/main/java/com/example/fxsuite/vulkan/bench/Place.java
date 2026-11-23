package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * Somewhere the avatar walks, to look at what stands there: what is in the avatar's way, as the engine's simulation
 * knows it; and what is drawn of the place each frame, on the flat ground every place has. What is in the way and
 * what is drawn are to come from the one description of what stands there, so the avatar never walks through what
 * is seen, nor into what is not. Closed, what it put on the card is gone from it.
 */
public interface Place extends AutoCloseable {

    /** What is in the avatar's way. */
    Obstacles inTheWay();

    /**
     * Whether it stands on the flat ground every walk has, level at nothing: yes, but for a place that brings its own
     * land, which the flat ground would cut through.
     */
    default boolean onTheFlat() {
        return true;
    }

    /**
     * Made ready for a frame seen by {@code camera}, before any of it is drawn: what is to be put on the card, or
     * taken off it, for that view, while nothing is being drawn. Most places stand as they are, and do nothing here.
     */
    default void readied(final Camera camera) {
    }

    /**
     * Whether, readied for the last view, everything to be drawn of it is on the card: yes, for a place put on the
     * card whole when it is made; a place that streams onto it as the eye moves says no till it has caught up. A film
     * waits for it before each frame, so no frame shows land not yet come.
     */
    default boolean settled() {
        return true;
    }

    /** What stands there, drawn by {@code pass} as {@code camera} sees it: all of it that hides what is behind it. */
    void drawn(Pass pass, Camera camera);

    /**
     * What stands there that is seen through, as glass is, drawn by {@code pass} as {@code camera} sees it. It is
     * asked for after everything else of a frame is drawn, the avatar too, since what is seen through is laid over
     * what is behind it; of several such things, a place draws the furthest from the eye first. Most places have
     * nothing of the kind, and draw nothing here.
     */
    default void seenThrough(final Pass pass, final Camera camera) {
    }

    @Override
    void close();
}

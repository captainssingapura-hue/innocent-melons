package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;

import java.util.function.Consumer;

/**
 * A picture of depths on the card, so many pixels wide and as many tall, drawn first and then looked up by the
 * looks of whatever is drawn after it: how near the nearest thing is at each place, seen from wherever the frame it
 * is drawn with sees from. Depth runs backwards in it as everywhere in the kit, from 1 at the nearest to nothing at
 * the furthest. Until it is first drawn on, and wherever nothing has been drawn on it, it holds the furthest there
 * is: nothing is nearer than anything.
 *
 * <p>A look asks it one thing: whether a depth is at least as near as the one kept at a place. What that means is
 * for whoever draws it and whoever asks: the sun's own picture of a frame, for shadows, is one use of it.
 */
public final class Depths implements AutoCloseable {

    private final Recording recording;
    private final int size;
    private final Sheet sheet;
    private final Kept<Of.Sampler> sampler;

    Depths(final Reach reach, final Recording recording, final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("a picture is some pixels wide and tall, not " + size);
        }
        this.recording = recording;
        this.size = size;
        this.sheet = new Sheet(reach, "a picture of depths to look up", Paper.DEPTH_LOOKED_UP, size, size);
        this.sampler = new Compared(reach).sampler();
        recording.begun().emptied(sheet, size);
        recording.sent();
    }

    /** How many pixels wide it is, and tall. */
    public int size() {
        return size;
    }

    /**
     * Drawn afresh, and done, all of it, before this comes back: the whole of it set to the furthest there is, and
     * whatever {@code drawing} draws drawn on it with {@code with}, told what {@code frame} holds. What draws is
     * handed to {@code drawing} and draws only until {@code drawing} is done.
     */
    public void drawn(final Tracing with, final Filling frame, final Consumer<Pass> drawing) {
        recording.begun().traced(sheet, size, frame, with.drawing(), drawing);
        recording.sent();
    }

    Kept<Of.View> view() {
        return sheet.view();
    }

    Kept<Of.Sampler> sampler() {
        return sampler;
    }

    @Override
    public void close() {
        sampler.close();
        sheet.close();
    }
}

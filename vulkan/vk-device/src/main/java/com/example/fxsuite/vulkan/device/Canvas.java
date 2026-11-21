package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.space.Vec3;

import java.nio.IntBuffer;
import java.util.function.Consumer;

import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_TRANSFER_DST_BIT;

/**
 * A picture drawn on the card with no window: so many pixels wide and tall, drawn exactly as a window is drawn, then
 * read back. What tests look at, since it needs no screen and its pixels can be asked one by one.
 */
public final class Canvas implements Target, AutoCloseable {

    private static final int PIXEL = 4;

    private final Recording recording;
    private final int width;
    private final int height;
    private final Sheet colours;
    private final Sheet depths;
    private final Bytes read;
    private boolean drawn;

    Canvas(final Reach reach, final Recording recording, final int width, final int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("a picture is some pixels wide and tall, not " + width + " by "
                    + height);
        }
        this.recording = recording;
        this.width = width;
        this.height = height;
        this.colours = new Sheet(reach, "a picture's colours", Paper.COLOUR, width, height);
        this.depths = new Sheet(reach, "a picture's depths", Paper.DEPTH, width, height);
        this.read = new Bytes(reach, "a picture read back", PIXEL * width * height,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    /** A frame drawn, and done, all of it, before this comes back. */
    @Override
    public void drawn(final Vec3 sky, final Filling frame, final Consumer<Pass> drawing) {
        final Strokes strokes = recording.begun();
        strokes.drawn(new Ground(colours.image(), colours.view(), depths, width, height), sky, frame, drawing);
        strokes.moved(colours.image(), Move.TO_BE_READ);
        strokes.read(colours.image(), read, width, height);
        recording.sent();
        drawn = true;
    }

    /**
     * The picture last drawn, read back: its pixels row by row from the top, each as a screen takes it, solid, then
     * red, green and blue, a byte each.
     */
    public int[] pixels() {
        if (!drawn) {
            throw new IllegalStateException("nothing has been drawn on this picture yet");
        }
        final IntBuffer kept = read.read().asIntBuffer();
        final int[] pixels = new int[kept.remaining()];
        kept.get(pixels);
        return pixels;
    }

    @Override
    public void close() {
        read.close();
        depths.close();
        colours.close();
    }
}

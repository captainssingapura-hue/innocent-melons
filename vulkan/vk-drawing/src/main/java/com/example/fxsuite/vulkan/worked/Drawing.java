package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.Arrays;

/**
 * A picture being worked out: its pixels, and how deep what is drawn at each lies. Depth runs backwards, as the
 * camera has it: nothing drawn is depth 0, and of two things at a pixel the one deeper in number is the nearer and
 * is kept.
 */
final class Drawing {

    private static final int BRIGHTEST = 255;
    private static final int SOLID = 0xff000000;

    private final int width;
    private final int[] pixels;
    private final double[] depths;

    /** {@code width} by {@code height}, nothing drawn: every pixel {@code behind}. */
    Drawing(final int width, final int height, final Vec3 behind) {
        this.width = width;
        this.pixels = new int[width * height];
        this.depths = new double[width * height];
        Arrays.fill(pixels, pixel(behind));
    }

    /** Whether something {@code depth} deep at ({@code x}, {@code y}) would show: nothing nearer is drawn there yet. */
    boolean shows(final int x, final int y, final double depth) {
        return depth > depths[width * y + x];
    }

    /** {@code colour}, {@code depth} deep, drawn at ({@code x}, {@code y}). */
    void draw(final int x, final int y, final double depth, final Vec3 colour) {
        pixels[width * y + x] = pixel(colour);
        depths[width * y + x] = depth;
    }

    Picture picture() {
        return new Picture(width, pixels);
    }

    /** A colour as a screen takes it: each of red, green and blue held from nothing to one, then a byte. */
    private int pixel(final Vec3 colour) {
        return SOLID | part(colour.x()) << 16 | part(colour.y()) << 8 | part(colour.z());
    }

    private int part(final double share) {
        return (int) Math.round(Math.clamp(share, 0, 1) * BRIGHTEST);
    }
}

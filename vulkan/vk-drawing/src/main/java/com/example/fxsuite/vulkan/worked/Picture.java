package com.example.fxsuite.vulkan.worked;

import java.awt.image.BufferedImage;

/**
 * A picture worked out: so many pixels wide and tall, each a colour as a screen takes it (red, green and blue, a
 * byte each). Never changed once made.
 */
public final class Picture {

    private static final int BYTE = 0xff;
    private static final int SOLID = 0xff000000;

    private final int width;
    private final int height;
    private final int[] pixels;

    /** A picture {@code width} wide, of {@code pixels}, row by row from the top. */
    public Picture(final int width, final int[] pixels) {
        if (width <= 0 || pixels.length == 0 || pixels.length % width != 0) {
            throw new IllegalArgumentException(pixels.length + " pixels do not make rows of " + width);
        }
        this.width = width;
        this.height = pixels.length / width;
        this.pixels = pixels.clone();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /** The pixel {@code x} across and {@code y} down, from the top left. */
    public int at(final int x, final int y) {
        return pixels[width * y + x];
    }

    /** How much red, green or blue ({@code which} 0, 1 or 2) the pixel there has, from 0 to 255. */
    public int part(final int x, final int y, final int which) {
        return at(x, y) >> 8 * (2 - which) & BYTE;
    }

    /** Half as wide and half as tall: each pixel the four it stands for, evened out, so edges are smooth. */
    public Picture halved() {
        final int[] made = new int[(width / 2) * (height / 2)];
        for (int y = 0; y < height / 2; y++) {
            for (int x = 0; x < width / 2; x++) {
                int pixel = SOLID;
                for (int which = 0; which < 3; which++) {
                    final int sum = part(2 * x, 2 * y, which) + part(2 * x + 1, 2 * y, which)
                            + part(2 * x, 2 * y + 1, which) + part(2 * x + 1, 2 * y + 1, which);
                    pixel |= (sum + 2) / 4 << 8 * (2 - which);
                }
                made[width / 2 * y + x] = pixel;
            }
        }
        return new Picture(width / 2, made);
    }

    /** As an image, to be saved or shown. */
    public BufferedImage image() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }
}

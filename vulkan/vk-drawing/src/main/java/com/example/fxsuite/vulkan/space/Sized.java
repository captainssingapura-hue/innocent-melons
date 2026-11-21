package com.example.fxsuite.vulkan.space;

/**
 * A stretching about a frame's middle: so many times as wide ({@code across}), as tall ({@code up}) and as deep
 * ({@code along}). One form, made once, is then drawn at any size: a box a unit each way as every box there is.
 */
public record Sized(double across, double up, double along) {

    /** The same every way: so many {@code times} as big. */
    public Sized(final double times) {
        this(times, times, times);
    }

    /** The matrix that stretches every point so. */
    public Mat4 matrix() {
        return new Mat4(new double[] {across, 0, 0, 0, 0, up, 0, 0, 0, 0, along, 0, 0, 0, 0, 1});
    }
}

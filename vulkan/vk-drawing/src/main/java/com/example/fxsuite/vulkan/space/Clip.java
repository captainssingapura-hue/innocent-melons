package com.example.fxsuite.vulkan.space;

/**
 * A point as a matrix leaves it, before it is divided through: where it falls across the picture ({@code x}), down it
 * ({@code y}), how deep ({@code z}), and what all three are divided by ({@code w}), which for a camera's matrix is how
 * far ahead of the camera the point is.
 */
public record Clip(double x, double y, double z, double w) {

    /**
     * Divided through, as the card divides it: across the picture from -1 at its left to 1 at its right, down it from
     * -1 at its top to 1 at its bottom, and its depth. Nothing behind the camera, or at it, is shown at all.
     */
    public Vec3 shown() {
        if (!(w > 0)) {
            throw new IllegalArgumentException("a point not ahead of the camera is not shown");
        }
        return new Vec3(x / w, y / w, z / w);
    }
}

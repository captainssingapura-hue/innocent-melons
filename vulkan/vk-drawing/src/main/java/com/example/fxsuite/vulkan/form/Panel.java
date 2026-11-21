package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A flat upright face, as a wall's is: how far across and how high, in centimetres, standing on its frame's ground
 * with its middle over the frame's middle, and facing back along the frame (toward whoever looks along it). Formed
 * in squares no bigger than {@code step}, so whatever is worked out over it is sampled evenly all over.
 */
public record Panel(double across, double up, double step) {

    public Panel {
        if (!(across > 0 && up > 0 && step > 0)) {
            throw new IllegalArgumentException("a face has some size, and its squares too: " + across + " by " + up
                    + ", in squares of " + step);
        }
    }

    /** Its face as triangles, two to a square. */
    public Mesh formed() {
        final int columns = (int) Math.ceil(across / step);
        final int rows = (int) Math.ceil(up / step);
        // what is behind it, for the triangles to face away from
        final Forming forming = new Forming(new Vec3(0, up / 2, 1));
        for (int row = 0; row <= rows; row++) {
            for (int column = 0; column <= columns; column++) {
                forming.point(new Vec3(across * column / columns - across / 2, up * row / rows, 0),
                        new Vec3(0, 0, -1));
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                final int corner = row * (columns + 1) + column;
                forming.triangle(corner, corner + 1, corner + columns + 1);
                forming.triangle(corner + 1, corner + columns + 2, corner + columns + 1);
            }
        }
        return forming.formed();
    }
}

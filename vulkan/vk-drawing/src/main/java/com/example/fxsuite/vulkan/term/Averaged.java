package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * What a share comes to over the whole of a thing's surface, worked out by Java, not stated by hand: the share
 * sampled at points spread evenly over every triangle of the thing's form, each counted for as much surface as it
 * stands for. The same points every time, so the same answer every time.
 */
public record Averaged(Mesh over) {

    /** Each triangle is sampled in this many rows, the rows' squared in points. */
    private static final int ROWS = 4;

    /** What {@code share} comes to over the thing: from nothing to one. */
    public double of(final Real share) {
        final Seen seen = new Seen();
        final Reckoning reckoning = new Reckoning(share.node());
        double sum = 0;
        for (int triangle = 0; triangle < over.triangles(); triangle++) {
            double within = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int step = 0; step < ROWS - row; step++) {
                    within += reckoning.at(seen.sample(inside(triangle, row + 1.0 / 3, step + 1.0 / 3), 0))[0];
                    if (step < ROWS - row - 1) {
                        within += reckoning.at(seen.sample(inside(triangle, row + 2.0 / 3, step + 2.0 / 3), 0))[0];
                    }
                }
            }
            sum += within / (ROWS * ROWS) * over.area(triangle);
        }
        return sum / over.area();
    }

    /**
     * The place on the surface of {@code triangle} so many rows from its first corner toward its second, steps
     * toward its third.
     */
    private Vec3 inside(final int triangle, final double rows, final double steps) {
        final Vec3 first = over.on(triangle, 0);
        return first.plus(over.on(triangle, 1).minus(first).times(rows / ROWS))
                .plus(over.on(triangle, 2).minus(first).times(steps / ROWS));
    }
}

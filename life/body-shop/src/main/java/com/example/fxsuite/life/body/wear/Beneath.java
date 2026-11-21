package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;

/**
 * What a garment is fitted over, as the body is held to be bound ({@link com.example.fxsuite.life.body.BoundPose}):
 * a surface of triangles, whatever it is made of, the muscles and the bones where no muscle covers them, a skin, or
 * what is worn under it; for each point, the part it goes with most and how much it follows each part as the body
 * moves ({@link Weights}), so that what is fitted over it follows it the same.
 */
public record Beneath(float[] points, int[] triangles, Part[] main, Weights[] weights) {

    /** How many points it has. */
    public int size() {
        return points.length / 3;
    }

    /** Point {@code i}. */
    public V3 point(final int i) {
        return new V3(points[3 * i], points[3 * i + 1], points[3 * i + 2]);
    }
}

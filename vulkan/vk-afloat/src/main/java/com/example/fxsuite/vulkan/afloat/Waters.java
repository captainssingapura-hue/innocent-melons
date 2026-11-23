package com.example.fxsuite.vulkan.afloat;

import com.example.fxsuite.vulkan.flight.Below;

/**
 * Where a place's water lies: how high its top is at a point, not a number where there is none, and how high the bed
 * under it is. In centimetres, height counted up, in the renderer's frame. Read, never changed. Any place that has
 * water may say so, and a boat may go wherever it is deep enough.
 */
public interface Waters {

    /** How high the water's top is at ({@code x}, {@code z}): not a number where there is no water. */
    double top(double x, double z);

    /** How high the ground under it is at ({@code x}, {@code z}). */
    double bed(double x, double z);

    /** How deep the water is at ({@code x}, {@code z}): nothing where there is none. */
    default double deep(final double x, final double z) {
        final double top = top(x, z);
        return Double.isNaN(top) ? 0 : Math.max(0, top - bed(x, z));
    }

    /** What lies below, for a camera to keep over: the water's top, or the ground where it is higher. */
    default Below below() {
        return (x, z) -> {
            final double top = top(x, z);
            return Double.isNaN(top) ? bed(x, z) : Math.max(top, bed(x, z));
        };
    }
}

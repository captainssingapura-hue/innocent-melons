package com.example.fxsuite.vulkan.flight;

/**
 * What is below a point flown over: how high the top of whatever lies there is, the ground or the water over it,
 * whichever is higher, in centimetres, height counted up, in the renderer's frame. Read, never changed: the flight
 * keeps above it and does nothing else with it.
 */
public interface Below {

    /** How high whatever lies at ({@code x}, {@code z}) reaches. */
    double under(double x, double z);
}

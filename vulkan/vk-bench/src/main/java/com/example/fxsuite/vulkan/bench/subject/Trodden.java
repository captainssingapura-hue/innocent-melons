package com.example.fxsuite.vulkan.bench.subject;

/**
 * The ground under a figure's feet, as whatever it walks on has it: asked of each foot apart, so that on a slope, a
 * stair or a curved deck a figure may stand with one foot higher than the other.
 */
public interface Trodden {

    /**
     * How much higher the ground is under the spot ({@code x}, {@code z}) of the engine's world than under the
     * figure's own middle, in centimetres; less than nothing, lower. Nothing where there is no ground within a step
     * of the figure's own, as past an edge.
     */
    double under(double x, double z);
}

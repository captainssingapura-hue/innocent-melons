package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * What a frame is told, for every look drawn in it: how the camera sees, and the light. Filled for a picture of a
 * given shape, since the camera's flattening depends on how wide the picture is for its height.
 */
public record Framed(Laid laid, Camera camera, Light light) {

    /** The frame's block, filled, for a picture {@code aspect} times as wide as it is tall. */
    public Filling filling(final double aspect) {
        return new Filling(laid.frame())
                .with("seen", camera.seen(aspect).columns())
                .with("sunTowards", three(light.sunTowards()))
                .with("sun", three(light.sun()))
                .with("ambient", three(light.ambient()));
    }

    private double[] three(final Vec3 of) {
        return new double[] {of.x(), of.y(), of.z()};
    }
}

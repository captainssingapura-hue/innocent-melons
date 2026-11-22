package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * What a frame is told, for every look drawn in it: how the camera sees, and the light. Filled for a picture of a
 * given shape, since the camera's flattening depends on how wide the picture is for its height; with shadows, or
 * with none; or filled for the sun's own picture of the frame, which is drawn first where there are shadows.
 */
public record Framed(Laid laid, Camera camera, Light light) {

    /** How many of the sun's picture's pixels a point is eased the way its surface faces, before it is looked up. */
    private static final double EASED = 2;
    /** How many of them nearer the sun it is then counted, so that what is exactly as near is in the sun. */
    private static final double NEARER = 0.5;

    /** The frame's block, filled, for a picture {@code aspect} times as wide as it is tall, with no shadows. */
    public Filling filling(final double aspect) {
        return lit(camera.seen(aspect)).with("beam", new double[16]).with("eased", 0, 0);
    }

    /**
     * The same, with shadows: each look looks up, in the sun's own picture drawn through {@code beam} and
     * {@code pixels} wide, how much of the sun reaches each point it draws.
     */
    public Filling filling(final double aspect, final Beam beam, final int pixels) {
        final double pixel = beam.pixel(pixels);
        return lit(camera.seen(aspect)).with("beam", fromTheEye(beam).columns())
                .with("eased", EASED * pixel, NEARER * pixel / (2 * beam.deep()));
    }

    /**
     * The frame's block for the sun's own picture, drawn through {@code beam}: things stand where they do for the
     * camera, the eye at the middle, and are seen from the sun.
     */
    public Filling beamed(final Beam beam) {
        return lit(fromTheEye(beam)).with("beam", new double[16]).with("eased", 0, 0);
    }

    /** Where the sun's picture has a point that is said as the camera sees it, the eye at the middle. */
    private Mat4 fromTheEye(final Beam beam) {
        return beam.seen().times(new Mat4(camera.eye()));
    }

    private Filling lit(final Mat4 seen) {
        return new Filling(laid.frame())
                .with("seen", seen.columns())
                .with("sunTowards", three(light.sunTowards()))
                .with("sun", three(light.sun()))
                .with("ambient", three(light.ambient()))
                .with("sky", three(light.sky()))
                .with("ground", three(light.ground()));
    }

    private double[] three(final Vec3 of) {
        return new double[] {of.x(), of.y(), of.z()};
    }
}

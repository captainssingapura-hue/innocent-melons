package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Where the world is seen from, and how: the eye, the three ways that are the camera's own (to its right, up of it,
 * ahead of it; each one long, square to the others), how wide it sees from top to bottom in degrees, and how near it
 * begins to see, in centimetres. It sees as far as there is anything: nothing is too far to be drawn.
 *
 * <p>Two things are done here as the card wants them, and nowhere else. A point is said as the camera sees it, the
 * eye taken from it in doubles ({@link #from}), before it is made single: so the card never works with the
 * kilometres between a thing and the world's middle. And depth runs backwards: 1 at the nearest the camera sees,
 * falling toward 0 with distance ({@link #projection}); singles are finest near nothing, so what is far off keeps
 * the fineness that depth running forwards would spend on what is under the camera's nose, and two faces a few
 * centimetres apart are told apart from kilometres away. The card is told to keep what is deeper in number, and to
 * clear its depth to 0.
 */
public record Camera(Vec3 eye, Vec3 right, Vec3 up, Vec3 ahead, double fieldOfView, double near) {

    public Camera {
        if (!(fieldOfView > 0 && fieldOfView < 180)) {
            throw new IllegalArgumentException("a camera sees more than nothing and less than half round: "
                    + fieldOfView);
        }
        if (!(near > 0)) {
            throw new IllegalArgumentException("a camera begins to see some way ahead of it: " + near);
        }
    }

    /** {@code placed}, in the world, as the camera sees it: how far from the eye, each way of the world's. */
    public Vec3 from(final Vec3 placed) {
        return placed.minus(eye);
    }

    /** The matrix for a thing standing at {@code where}, unturned: its points, to the world as the camera sees it. */
    public Mat4 placed(final Vec3 where) {
        return new Mat4(from(where));
    }

    /** The camera's turning: a point as the camera sees it, said by the camera's own three ways. */
    public Mat4 turned() {
        return new Mat4(right, up, ahead);
    }

    /**
     * The camera's flattening, for a picture {@code aspect} times as wide as it is tall: across the picture, down it
     * (the card counts down from the top), and depth running backwards from 1 at {@link #near}.
     */
    public Mat4 projection(final double aspect) {
        final double reach = 1 / Math.tan(Math.toRadians(fieldOfView) / 2);
        return new Mat4(new double[] {reach / aspect, 0, 0, 0, 0, -reach, 0, 0, 0, 0, 0, near, 0, 0, 1, 0});
    }

    /** Turning and flattening together: a point as the camera sees it, to where it falls in the picture. */
    public Mat4 seen(final double aspect) {
        return projection(aspect).times(turned());
    }
}

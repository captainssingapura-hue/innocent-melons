package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The sun's light as it falls on a part of the world, seen from where it comes from: a square of the world
 * {@code across} centimetres wide round {@code round}, looked at along the way the sun shines ({@code towards}),
 * and as much as {@code deep} centimetres nearer the sun than {@code round} and as much further. What is drawn
 * through it is the sun's own picture of that part of the world: how near the sun the nearest thing is, at each
 * place. A point is in the sun where nothing in that picture is nearer the sun than it; so shadows are worked out
 * from this and from nothing else.
 *
 * <p>Nothing here grows smaller with distance, since the sun's light all travels the one way. Depth runs backwards,
 * as the camera's does: 1 at the nearest to the sun the beam reaches, falling to nothing at the furthest. And a
 * triangle facing the sun goes round clockwise in its picture, as one facing the camera does in the camera's.
 */
public record Beam(Vec3 round, Vec3 towards, double across, double deep) {

    /** A sun shining more nearly straight down than this takes its sideways from the world's third way. */
    private static final double UPRIGHT = 0.99;

    public Beam {
        if (!(across > 0 && deep > 0) || !(towards.length() > 0)) {
            throw new IllegalArgumentException("a beam shines some way, on a square of some size, some way through: "
                    + towards + ", " + across + " across, " + deep + " deep");
        }
    }

    /** The way the sun shines, one long. */
    public Vec3 ahead() {
        return towards.unit();
    }

    /** To the right in the sun's picture: level, and square to the way the sun shines. */
    public Vec3 right() {
        final Vec3 ahead = ahead();
        return (Math.abs(ahead.y()) < UPRIGHT ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1)).cross(ahead).unit();
    }

    /** Up in the sun's picture: square to the other two. */
    public Vec3 up() {
        return ahead().cross(right());
    }

    /**
     * Where a point of the world falls in the sun's picture: from -1 to 1 across it and down it, and how near the
     * sun it is, from 1 at the nearest the beam reaches to nothing at the furthest.
     */
    public Mat4 seen() {
        final Mat4 flattened = new Mat4(new double[] {2 / across, 0, 0, 0, 0, -2 / across, 0, 0,
                0, 0, -1 / (2 * deep), 0.5, 0, 0, 0, 1});
        return flattened.times(new Mat4(right(), up(), ahead())).times(new Mat4(round.times(-1)));
    }

    /** How much of the world one pixel covers, in a picture of the beam {@code pixels} wide: in centimetres. */
    public double pixel(final int pixels) {
        return across / pixels;
    }

    /**
     * The same beam, moved by less than a pixel of a picture {@code pixels} wide, so that it lies a whole number of
     * pixels from the world's middle. A beam that follows something as it moves is settled each time: the picture's
     * pixels then fall on the same places of the world however it has moved, and a shadow's edge does not creep.
     */
    public Beam settled(final int pixels) {
        final double pixel = pixel(pixels);
        final Vec3 moved = right().times(Math.rint(round.dot(right()) / pixel) * pixel)
                .plus(up().times(Math.rint(round.dot(up()) / pixel) * pixel))
                .plus(ahead().times(round.dot(ahead())));
        return new Beam(moved, towards, across, deep);
    }
}

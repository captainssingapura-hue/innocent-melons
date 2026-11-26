package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Which way a craft is turned: its heading round from along the world (growing to the right), how far its nose is
 * up, and how far it is rolled, its right wing down as it grows; in degrees, turned in that order, the heading
 * first. Its own three ways follow from them: ahead, along its nose; up, through its roof; and to its right.
 *
 * @param heading how far round
 * @param pitch   how far its nose is up
 * @param roll    how far its right wing is down
 */
public record Attitude(double heading, double pitch, double roll) {

    /** Level, along the world. */
    public Attitude() {
        this(0, 0, 0);
    }

    /** Its own way {@code local} (across, up, ahead, in its own frame), in the world. */
    public Vec3 turned(final Vec3 local) {
        final double h = Math.toRadians(heading);
        final double p = Math.toRadians(pitch);
        final double r = Math.toRadians(roll);
        // rolled about ahead, then pitched about across (nose up), then turned about up (to the right)
        final Vec3 rolled = new Vec3(local.x() * Math.cos(r) + local.y() * Math.sin(r),
                -local.x() * Math.sin(r) + local.y() * Math.cos(r), local.z());
        final Vec3 pitched = new Vec3(rolled.x(), rolled.y() * Math.cos(p) + rolled.z() * Math.sin(p),
                -rolled.y() * Math.sin(p) + rolled.z() * Math.cos(p));
        return new Vec3(pitched.x() * Math.cos(h) + pitched.z() * Math.sin(h), pitched.y(),
                -pitched.x() * Math.sin(h) + pitched.z() * Math.cos(h));
    }

    /** Along its nose. */
    public Vec3 ahead() {
        return turned(new Vec3(0, 0, 1));
    }

    /** Through its roof. */
    public Vec3 up() {
        return turned(new Vec3(0, 1, 0));
    }

    /** To its right. */
    public Vec3 right() {
        return turned(new Vec3(1, 0, 0));
    }

    /** What takes its own frame to the world, standing at {@code at}. */
    public Mat4 placed(final Vec3 at) {
        final Vec3 x = right();
        final Vec3 y = up();
        final Vec3 z = ahead();
        return new Mat4(new double[] {x.x(), y.x(), z.x(), at.x(), x.y(), y.y(), z.y(), at.y(),
            x.z(), y.z(), z.z(), at.z(), 0, 0, 0, 1});
    }

    /** Between {@code before} and this, {@code blend} of the way on. */
    public Attitude between(final Attitude before, final double blend) {
        return new Attitude(before.heading + (heading - before.heading) * blend,
                before.pitch + (pitch - before.pitch) * blend, before.roll + (roll - before.roll) * blend);
    }
}

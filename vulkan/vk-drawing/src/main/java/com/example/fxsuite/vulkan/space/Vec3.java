package com.example.fxsuite.vulkan.space;

/**
 * A point, or a way and how far along it, in the game's frame: across to the right ({@code x}), up ({@code y}), along
 * ahead ({@code z}); in centimetres. Worked in doubles: what reaches the card is made single only once it is as the
 * camera sees it, so a thing kilometres from the world's middle is still placed to the centimetre.
 */
public record Vec3(double x, double y, double z) {

    public Vec3 plus(final Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 minus(final Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    /** As far again, {@code by} times, the same way. */
    public Vec3 times(final double by) {
        return new Vec3(x * by, y * by, z * by);
    }

    /** How much of each lies along the other: nothing, where they are square to each other. */
    public double dot(final Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * A way square to both, by the usual rule of three: {@code x} crossed with {@code y} is {@code z}. In this frame
     * (up crossed with ahead is across), what is up of a camera and what is ahead of it give what is to its right.
     */
    public Vec3 cross(final Vec3 other) {
        return new Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    /** The same way, one long. A way of no length has no way to keep, and is refused. */
    public Vec3 unit() {
        final double length = length();
        if (!(length > 0)) {
            throw new IllegalArgumentException("a way of no length points nowhere");
        }
        return times(1 / length);
    }
}

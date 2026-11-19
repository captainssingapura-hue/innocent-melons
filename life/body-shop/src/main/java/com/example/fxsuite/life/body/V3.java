package com.example.fxsuite.life.body;

/**
 * A point or a direction in a body's own frame, in centimetres: {@code x} to its right, {@code y} up, {@code z} ahead,
 * the way it faces. Its feet on the ground at {@code y} 0, between them its origin.
 */
public record V3(double x, double y, double z) {

    public static final V3 ZERO = new V3(0, 0, 0);
    public static final V3 X = new V3(1, 0, 0);
    public static final V3 Y = new V3(0, 1, 0);
    public static final V3 Z = new V3(0, 0, 1);

    public V3 plus(final V3 o) {
        return new V3(x + o.x, y + o.y, z + o.z);
    }

    public V3 minus(final V3 o) {
        return new V3(x - o.x, y - o.y, z - o.z);
    }

    public V3 times(final double k) {
        return new V3(x * k, y * k, z * k);
    }

    public double dot(final V3 o) {
        return x * o.x + y * o.y + z * o.z;
    }

    public V3 cross(final V3 o) {
        return new V3(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    /** This direction, a centimetre long. */
    public V3 unit() {
        final double l = length();
        return new V3(x / l, y / l, z / l);
    }

    /** Whether it is a point anywhere at all: no part of it not a number, or past every number. */
    public boolean finite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }
}

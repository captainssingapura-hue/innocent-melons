package com.example.fxsuite.vulkan.space;

/**
 * A turning about a line through a frame's middle: {@code about} is the way the line runs, and {@code degrees} how
 * far round it. Its matrix is the one arithmetic gives for that axis and angle, the same the engine's own figure is
 * turned by, so a joint's angle means here what it means there.
 */
public record Turned(Vec3 about, double degrees) {

    /** The matrix that turns every point so. */
    public Mat4 matrix() {
        final Vec3 axis = about.unit();
        final double cos = Math.cos(Math.toRadians(degrees));
        final double sin = Math.sin(Math.toRadians(degrees));
        final double rest = 1 - cos;
        final double x = axis.x();
        final double y = axis.y();
        final double z = axis.z();
        return new Mat4(new double[] {
                cos + x * x * rest, x * y * rest - z * sin, x * z * rest + y * sin, 0,
                y * x * rest + z * sin, cos + y * y * rest, y * z * rest - x * sin, 0,
                z * x * rest - y * sin, z * y * rest + x * sin, cos + z * z * rest, 0,
                0, 0, 0, 1});
    }

    /** The matrix that turns every point so about a line through {@code pivot} and not through the middle. */
    public Mat4 matrix(final Vec3 pivot) {
        return new Mat4(pivot).times(matrix()).times(new Mat4(pivot.times(-1)));
    }
}

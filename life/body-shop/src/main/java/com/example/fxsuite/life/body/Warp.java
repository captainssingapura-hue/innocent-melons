package com.example.fxsuite.life.body;

/**
 * A smooth bending of space through points ({@link ThinPlate}): a point taken by an affine map, and by each of the
 * points it was made through as far as its weight there, falling off with the distance from it, so that each of them
 * goes where it was asked to and the space between bends with them as little as it may.
 *
 * @param at     the points it was made through, where they were
 * @param weight each one's weight, along x, y and z
 * @param affine the affine map, x, y and z each a constant and its share of the point's x, y and z
 */
public record Warp(V3[] at, V3[] weight, double[] affine) {

    /** Where {@code p} goes. */
    public V3 apply(final V3 p) {
        double x = affine[0] + affine[1] * p.x() + affine[2] * p.y() + affine[3] * p.z();
        double y = affine[4] + affine[5] * p.x() + affine[6] * p.y() + affine[7] * p.z();
        double z = affine[8] + affine[9] * p.x() + affine[10] * p.y() + affine[11] * p.z();
        for (int i = 0; i < at.length; i++) {
            final double r = p.minus(at[i]).length();
            x += weight[i].x() * r;
            y += weight[i].y() * r;
            z += weight[i].z() * r;
        }
        return new V3(x, y, z);
    }
}

package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A turn in a body's frame ({@link V3}): a rotation, as the matrix that does it, row by row. Turns about the three
 * ways by so many degrees, each by the right-hand rule: about {@code x} (right) a positive turn tips up toward ahead;
 * about {@code y} (up), ahead toward the right; about {@code z} (ahead), up toward the left.
 */
public record Rot(double xx, double xy, double xz, double yx, double yy, double yz, double zx, double zy, double zz)
        implements Scalar {

    public static final Rot NONE = new Rot(1, 0, 0, 0, 1, 0, 0, 0, 1);

    /** {@code degrees} about the right-hand way, {@code x}. */
    public static Rot aboutX(final double degrees) {
        final double c = Math.cos(Math.toRadians(degrees));
        final double s = Math.sin(Math.toRadians(degrees));
        return new Rot(1, 0, 0, 0, c, -s, 0, s, c);
    }

    /** {@code degrees} about the up way, {@code y}: positive takes ahead toward the right. */
    public static Rot aboutY(final double degrees) {
        final double c = Math.cos(Math.toRadians(degrees));
        final double s = Math.sin(Math.toRadians(degrees));
        return new Rot(c, 0, s, 0, 1, 0, -s, 0, c);
    }

    /** {@code degrees} about the ahead way, {@code z}: positive takes up toward the left. */
    public static Rot aboutZ(final double degrees) {
        final double c = Math.cos(Math.toRadians(degrees));
        final double s = Math.sin(Math.toRadians(degrees));
        return new Rot(c, -s, 0, s, c, 0, 0, 0, 1);
    }

    /** This turn after {@code then}: {@code then} done first, in this one's frame. */
    public Rot then(final Rot o) {
        return new Rot(
                xx * o.xx + xy * o.yx + xz * o.zx, xx * o.xy + xy * o.yy + xz * o.zy, xx * o.xz + xy * o.yz + xz * o.zz,
                yx * o.xx + yy * o.yx + yz * o.zx, yx * o.xy + yy * o.yy + yz * o.zy, yx * o.xz + yy * o.yz + yz * o.zz,
                zx * o.xx + zy * o.yx + zz * o.zx, zx * o.xy + zy * o.yy + zz * o.zy, zx * o.xz + zy * o.yz + zz * o.zz);
    }

    /** {@code degrees} about {@code axis} (a direction, any length), by the right-hand rule. */
    public static Rot about(final V3 axis, final double degrees) {
        final V3 u = axis.unit();
        final double c = Math.cos(Math.toRadians(degrees));
        final double s = Math.sin(Math.toRadians(degrees));
        final double t = 1 - c;
        return new Rot(
                t * u.x() * u.x() + c, t * u.x() * u.y() - s * u.z(), t * u.x() * u.z() + s * u.y(),
                t * u.x() * u.y() + s * u.z(), t * u.y() * u.y() + c, t * u.y() * u.z() - s * u.x(),
                t * u.x() * u.z() - s * u.y(), t * u.y() * u.z() + s * u.x(), t * u.z() * u.z() + c);
    }

    /** The least turn that takes direction {@code from} to direction {@code to}. */
    public static Rot between(final V3 from, final V3 to) {
        final V3 a = from.unit();
        final V3 b = to.unit();
        final double cos = Math.clamp(a.dot(b), -1, 1);
        final V3 axis = a.cross(b);
        if (axis.length() < 1e-9) {
            // the same way, or the opposite: none, or half round any way square to it
            return cos > 0 ? NONE : about(Math.abs(a.x()) < 0.9 ? a.cross(V3.X) : a.cross(V3.Y), 180);
        }
        return about(axis, Math.toDegrees(Math.acos(cos)));
    }

    /** The turn back: this one undone. */
    public Rot back() {
        return new Rot(xx, yx, zx, xy, yy, zy, xz, yz, zz);
    }

    /** This turn as it is seen in a mirror across the body's middle ({@code x} the other way): a left side's. */
    public Rot mirrored() {
        return new Rot(xx, -xy, -xz, -yx, yy, yz, -zx, zy, zz);
    }

    /** {@code v} turned. */
    public V3 apply(final V3 v) {
        return new V3(xx * v.x() + xy * v.y() + xz * v.z(), yx * v.x() + yy * v.y() + yz * v.z(),
                zx * v.x() + zy * v.y() + zz * v.z());
    }
}

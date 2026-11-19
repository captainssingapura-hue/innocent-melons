package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * The shapes a body's parts are made of, each a closed surface ({@link Surface}), in a bone's own frame: a capsule
 * along a line, thicker or thinner along it, with round ends; an ellipsoid, or the top of one; a flared skirt; a box.
 * Pure geometry, nothing stored.
 */
final class Shapes {

    /** How many rings round each round end of a capsule. */
    static final int CAP = 4;

    private Shapes() {
    }

    /**
     * A capsule from {@code a} to {@code b}, round, as thick across as {@code radii} say along it (its first at
     * {@code a}, its last at {@code b}, the rest evenly between), {@code flat} times as deep front to back as across;
     * its ends rounded, as far out as its radius there.
     */
    static Surface capsule(final String name, final Paint paint, final V3 a, final V3 b, final double[] radii,
                           final double flat, final int around) {
        return capsule(name, paint, a, b, radii, flat, 1, around);
    }

    /**
     * A capsule as above, its ends domed only {@code ends} times as far out along it as its radius there: low, as a
     * body's trunk is across its shoulders, under its belly.
     */
    static Surface capsule(final String name, final Paint paint, final V3 a, final V3 b, final double[] radii,
                           final double flat, final double ends, final int around) {
        final V3 u = b.minus(a).unit();
        final V3 across = u.cross(Math.abs(u.z()) < 0.9 ? V3.Z : V3.Y).unit();
        final V3 front = across.cross(u).unit();
        final double r0 = radii[0];
        final double r1 = radii[radii.length - 1];
        final List<List<V3>> rings = new ArrayList<>();
        for (int j = 1; j < CAP; j++) {
            final double phi = Math.PI / 2 * j / CAP;
            rings.add(ring(a.minus(u.times(ends * r0 * Math.cos(phi))), across, front, r0 * Math.sin(phi), flat,
                    around));
        }
        for (int k = 0; k < radii.length; k++) {
            final double t = (double) k / (radii.length - 1);
            rings.add(ring(a.plus(b.minus(a).times(t)), across, front, radii[k], flat, around));
        }
        for (int j = CAP - 1; j >= 1; j--) {
            final double phi = Math.PI / 2 * j / CAP;
            rings.add(ring(b.plus(u.times(ends * r1 * Math.cos(phi))), across, front, r1 * Math.sin(phi), flat,
                    around));
        }
        return new Surface(name, paint, rings, a.minus(u.times(ends * r0)), b.plus(u.times(ends * r1)), true);
    }

    /** A capsule as thick all along. */
    static Surface capsule(final String name, final Paint paint, final V3 a, final V3 b, final double r,
                           final double flat, final int around) {
        return capsule(name, paint, a, b, new double[] {r, r}, flat, around);
    }

    /** A ring round {@code at}, across and front of it, {@code r} out across, {@code r * flat} out to the front. */
    private static List<V3> ring(final V3 at, final V3 across, final V3 front, final double r, final double flat,
                                 final int around) {
        final List<V3> ring = new ArrayList<>(around);
        for (int k = 0; k < around; k++) {
            final double t = 2 * Math.PI * k / around;
            ring.add(at.plus(across.times(r * Math.cos(t))).plus(front.times(r * flat * Math.sin(t))));
        }
        return ring;
    }

    /** An ellipsoid round {@code c}, so far out across ({@code x}), up ({@code y}) and ahead ({@code z}). */
    static Surface ellipsoid(final String name, final Paint paint, final V3 c, final double rx, final double ry,
                             final double rz, final int around, final int bands) {
        return dome(name, paint, c, rx, ry, rz, -90, around, bands);
    }

    /**
     * The top of an ellipsoid round {@code c}, from latitude {@code from} (degrees, -90 its bottom) up to its top; its
     * bottom closed flat across, on a point of that latitude's height under its middle.
     */
    static Surface dome(final String name, final Paint paint, final V3 c, final double rx, final double ry,
                        final double rz, final double from, final int around, final int bands) {
        final List<List<V3>> rings = new ArrayList<>();
        final boolean whole = from <= -90;
        for (int i = whole ? 1 : 0; i < bands; i++) {
            final double lat = Math.toRadians(from + (90 - from) * i / bands);
            final List<V3> ring = new ArrayList<>(around);
            for (int k = 0; k < around; k++) {
                final double t = 2 * Math.PI * k / around;
                ring.add(c.plus(new V3(rx * Math.cos(lat) * Math.cos(t), ry * Math.sin(lat),
                        rz * Math.cos(lat) * Math.sin(t))));
            }
            rings.add(ring);
        }
        final V3 bottom = c.plus(new V3(0, ry * Math.sin(Math.toRadians(from)), 0));
        return new Surface(name, paint, rings, bottom, c.plus(new V3(0, ry, 0)), true);
    }

    /**
     * A skirt flaring down from {@code top} to {@code bottom} under it: round it so far across and front to back at
     * each; closed flat at both.
     */
    static Surface skirt(final String name, final Paint paint, final V3 top, final double rx0, final double rz0,
                         final V3 bottom, final double rx1, final double rz1, final int around) {
        final List<List<V3>> rings = new ArrayList<>();
        final int steps = 4;
        for (int i = 0; i <= steps; i++) {
            final double s = (double) i / steps;
            final V3 at = top.plus(bottom.minus(top).times(s));
            final double rx = rx0 + (rx1 - rx0) * s;
            final double rz = rz0 + (rz1 - rz0) * s;
            final List<V3> ring = new ArrayList<>(around);
            for (int k = 0; k < around; k++) {
                final double t = 2 * Math.PI * k / around;
                ring.add(at.plus(new V3(rx * Math.cos(t), 0, rz * Math.sin(t))));
            }
            rings.add(ring);
        }
        return new Surface(name, paint, rings, top, bottom, true);
    }

    /** A box round {@code c}, so wide across, so high, so deep: flat-faced. */
    static Surface box(final String name, final Paint paint, final V3 c, final double w, final double h,
                       final double d) {
        final double x = w / 2;
        final double y = h / 2;
        final double z = d / 2;
        final List<List<V3>> rings = new ArrayList<>();
        for (final double up : new double[] {-y, y}) {
            rings.add(List.of(c.plus(new V3(-x, up, -z)), c.plus(new V3(x, up, -z)), c.plus(new V3(x, up, z)),
                    c.plus(new V3(-x, up, z))));
        }
        return new Surface(name, paint, rings, c.plus(new V3(0, -y, 0)), c.plus(new V3(0, y, 0)), false);
    }
}

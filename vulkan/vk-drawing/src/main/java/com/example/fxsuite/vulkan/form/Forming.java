package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A mesh being put together, a point and a triangle at a time. Whoever forms a thing need not mind which way round
 * its triangles go: each is turned, as it is added, to face out. For a thing that is whole and has a middle, out is
 * away from the middle ({@link #triangle}); for a thing that has none, as walls round a room have none, out is the
 * way the triangle's own points say their surface faces ({@link #faced}).
 */
public final class Forming {

    private final Vec3 middle;
    private final List<Vec3> points = new ArrayList<>();
    private final List<Vec3> facings = new ArrayList<>();
    private final List<Vec3> ons = new ArrayList<>();
    private final List<Integer> corners = new ArrayList<>();

    /** A form whose surface faces away from {@code middle}. */
    public Forming(final Vec3 middle) {
        this.middle = middle;
    }

    /** A point at {@code where}, its surface facing {@code facing} there: its number. */
    public int point(final Vec3 where, final Vec3 facing) {
        return point(where, facing, where);
    }

    /**
     * A point at {@code where}, its surface facing {@code facing} there, and {@code on} where on the thing's surface
     * it is, for the surface's terms: its number.
     */
    public int point(final Vec3 where, final Vec3 facing, final Vec3 on) {
        points.add(where);
        facings.add(facing.unit());
        ons.add(on);
        return points.size() - 1;
    }

    /** A triangle with its corners at points {@code a}, {@code b} and {@code c}, turned to face away from the middle. */
    public void triangle(final int a, final int b, final int c) {
        final Vec3 first = points.get(a);
        final Vec3 out = first.plus(points.get(b)).plus(points.get(c)).times(1.0 / 3).minus(middle);
        wound(a, b, c, out);
    }

    /**
     * A triangle with its corners at points {@code a}, {@code b} and {@code c}, turned to face the way its first
     * point says its surface faces.
     */
    public void faced(final int a, final int b, final int c) {
        wound(a, b, c, facings.get(a));
    }

    private void wound(final int a, final int b, final int c, final Vec3 out) {
        final Vec3 first = points.get(a);
        final Vec3 crossed = points.get(b).minus(first).cross(points.get(c).minus(first));
        final boolean facesOut = crossed.dot(out) > 0;
        corners.add(a);
        corners.add(facesOut ? b : c);
        corners.add(facesOut ? c : b);
    }

    /** What has been put together. */
    public Mesh formed() {
        return new Mesh(points, facings, corners, ons);
    }
}

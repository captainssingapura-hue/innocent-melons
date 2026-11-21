package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A mesh being put together, a point and a triangle at a time, round a middle its surface faces away from. Whoever
 * forms a thing need not mind which way round its triangles go: each is turned, as it is added, to face out.
 */
public final class Forming {

    private final Vec3 middle;
    private final List<Vec3> points = new ArrayList<>();
    private final List<Vec3> facings = new ArrayList<>();
    private final List<Integer> corners = new ArrayList<>();

    /** A form whose surface faces away from {@code middle}. */
    public Forming(final Vec3 middle) {
        this.middle = middle;
    }

    /** A point at {@code where}, its surface facing {@code facing} there: its number. */
    public int point(final Vec3 where, final Vec3 facing) {
        points.add(where);
        facings.add(facing.unit());
        return points.size() - 1;
    }

    /** A triangle with its corners at points {@code a}, {@code b} and {@code c}, turned to face out. */
    public void triangle(final int a, final int b, final int c) {
        final Vec3 first = points.get(a);
        final Vec3 crossed = points.get(b).minus(first).cross(points.get(c).minus(first));
        final Vec3 out = first.plus(points.get(b)).plus(points.get(c)).times(1.0 / 3).minus(middle);
        final boolean facesOut = crossed.dot(out) > 0;
        corners.add(a);
        corners.add(facesOut ? b : c);
        corners.add(facesOut ? c : b);
    }

    /** What has been put together. */
    public Mesh formed() {
        return new Mesh(points, facings, corners);
    }
}

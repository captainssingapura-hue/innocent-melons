package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * A surface of the body's, as the body shop finds one, its points three numbers each and its triangles three points
 * each, made ready for the card, which draws only the face of a triangle its corners go round to the left of, seen
 * from outside ({@code (b - a) x (c - a)} out). Which way round the body shop's go is not said, so it is found once
 * for the whole surface: by the room it closes in, which is more than none only if its triangles face out, the right
 * way round, or else turned. Each point's way out found from the triangles about it, the bigger counting for more.
 * Any of its triangles can then be taken apart from the rest, a part of the body's say: what is taken keeps the
 * whole's way out at its edges, so two parts of one surface meet smoothly, as they were.
 *
 * <p>Where on its surface a point is, for a material's pattern, is where it is: the body is drawn still.
 */
public final class Sheet {

    private final float[] points;
    private final int[] triangles;
    private final boolean turned;
    private final float[] out;

    /** The surface of {@code points} and {@code triangles}, by number. */
    public Sheet(final float[] points, final int[] triangles) {
        this.points = points;
        this.triangles = triangles;
        this.turned = closes(points, triangles) < 0;
        this.out = outs();
    }

    /** How many triangles it has. */
    public int triangleCount() {
        return triangles.length / 3;
    }

    /** Where point {@code i} is. */
    public Vec3 point(final int i) {
        return new Vec3(points[3 * i], points[3 * i + 1], points[3 * i + 2]);
    }

    /** The point at corner {@code corner} (0, 1 or 2) of {@code triangle}, by number. */
    public int corner(final int triangle, final int corner) {
        return triangles[3 * triangle + corner];
    }

    /** Whether its triangles went round the other way and are turned. */
    public boolean turned() {
        return turned;
    }

    /** The whole of it, for the card. */
    public Mesh mesh() {
        return mesh(t -> true);
    }

    /** Its triangles {@code kept} says to keep, by number, for the card: none, if none is. */
    public Mesh mesh(final IntPredicate kept) {
        final List<Vec3> at = new ArrayList<>();
        final List<Vec3> facing = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>();
        final Map<Integer, Integer> taken = new HashMap<>();
        for (int t = 0; t < triangleCount(); t++) {
            if (!kept.test(t)) {
                continue;
            }
            for (int k = 0; k < 3; k++) {
                final int i = triangles[3 * t + (turned && k > 0 ? 3 - k : k)];
                corners.add(taken.computeIfAbsent(i, j -> {
                    at.add(point(j));
                    facing.add(new Vec3(out[3 * j], out[3 * j + 1], out[3 * j + 2]));
                    return at.size() - 1;
                }));
            }
        }
        return new Mesh(at, facing, corners);
    }

    /**
     * Six times the room {@code triangles} close in, by the way they go round: more than none if they face out as the
     * card has them, less if the other way. Taken about the surface's middle, so it is found as well far from nought.
     */
    private double closes(final float[] p, final int[] t) {
        double x = 0;
        double y = 0;
        double z = 0;
        final int n = p.length / 3;
        for (int i = 0; i < n; i++) {
            x += p[3 * i];
            y += p[3 * i + 1];
            z += p[3 * i + 2];
        }
        final Vec3 middle = new Vec3(x / Math.max(1, n), y / Math.max(1, n), z / Math.max(1, n));
        double room = 0;
        for (int k = 0; k < t.length; k += 3) {
            final Vec3 a = new Vec3(p[3 * t[k]], p[3 * t[k] + 1], p[3 * t[k] + 2]).minus(middle);
            final Vec3 b = new Vec3(p[3 * t[k + 1]], p[3 * t[k + 1] + 1], p[3 * t[k + 1] + 2]).minus(middle);
            final Vec3 c = new Vec3(p[3 * t[k + 2]], p[3 * t[k + 2] + 1], p[3 * t[k + 2] + 2]).minus(middle);
            room += a.dot(b.cross(c));
        }
        return room;
    }

    /** Each point's way out: the triangles about it, each facing out and counting for as big as it is. */
    private float[] outs() {
        final double[] sum = new double[points.length];
        for (int t = 0; t < triangleCount(); t++) {
            final Vec3 a = point(triangles[3 * t]);
            final Vec3 crossed = point(triangles[3 * t + 1]).minus(a).cross(point(triangles[3 * t + 2]).minus(a))
                    .times(turned ? -1 : 1);
            for (int k = 0; k < 3; k++) {
                final int i = triangles[3 * t + k];
                sum[3 * i] += crossed.x();
                sum[3 * i + 1] += crossed.y();
                sum[3 * i + 2] += crossed.z();
            }
        }
        final float[] made = new float[points.length];
        for (int i = 0; i < made.length; i += 3) {
            final double length = Math.sqrt(sum[i] * sum[i] + sum[i + 1] * sum[i + 1] + sum[i + 2] * sum[i + 2]);
            final double k = length > 0 ? 1 / length : 0;
            made[i] = (float) (sum[i] * k);
            made[i + 1] = (float) (k > 0 ? sum[i + 1] * k : 1);
            made[i + 2] = (float) (sum[i + 2] * k);
        }
        return made;
    }
}

package com.example.fxsuite.building;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.ArrayList;
import java.util.List;

/**
 * Timbers, boards and blocks as solids ({@link Solid}), in a structure's own frame (across, along, up; centimetres): a
 * squared member from one point to another in a plane across, a block square to the frame, a board whose top follows a
 * slope, a slab between any two heights at each end, a block battered front and back, a strut across, a wedge.
 */
public final class Timbers {

    private Timbers() {
    }

    /**
     * A squared member {@code side} thick, its middle line from ({@code z0}, {@code y0}) to ({@code z1}, {@code y1})
     * along and up, at {@code x} across: its sides square to its line.
     */
    public static Solid member(final String name, final Role role, final Material material, final double x,
                               final double z0, final double y0, final double z1, final double y1, final double side) {
        final double l = Math.hypot(z1 - z0, y1 - y0);
        // square to its line in the plane along and up
        final double nz = -(y1 - y0) / l * side / 2;
        final double ny = (z1 - z0) / l * side / 2;
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean end = (k & 1) != 0;
            final double cx = x + ((k & 2) != 0 ? side / 2 : -side / 2);
            final double s = (k & 4) != 0 ? 1 : -1;
            corners.add(new Point(cx, (end ? z1 : z0) + s * nz, (end ? y1 : y0) + s * ny));
        }
        return new Solid(name, role, material, corners);
    }

    /** A block square to the frame: x0 to x1 across, z0 to z1 along, y0 up to y1. */
    public static Solid block(final String name, final Role role, final Material material, final double x0,
                              final double x1, final double z0, final double z1, final double y0, final double y1) {
        return slab(name, role, material, x0, x1, z0, z1, y0, y0, y1, y1);
    }

    /**
     * A board across from {@code x0} to {@code x1}, along from {@code z0} to {@code z1}, its top at {@code top0} at
     * the one end and {@code top1} at the other, {@code thick} under it: a deck's plank or a roof's tiles on a slope.
     */
    public static Solid board(final String name, final Role role, final Material material, final double x0,
                              final double x1, final double z0, final double z1, final double top0, final double top1,
                              final double thick) {
        return slab(name, role, material, x0, x1, z0, z1, top0 - thick, top1 - thick, top0, top1);
    }

    /**
     * A slab across from {@code x0} to {@code x1}, along from {@code z0} to {@code z1}: at the one end from
     * {@code bottom0} up to {@code top0}, at the other from {@code bottom1} to {@code top1}: a gable's wall under a
     * roof's slope.
     */
    public static Solid slab(final String name, final Role role, final Material material, final double x0,
                             final double x1, final double z0, final double z1, final double bottom0,
                             final double bottom1, final double top0, final double top1) {
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean end = (k & 2) != 0;
            final boolean up = (k & 4) != 0;
            corners.add(new Point((k & 1) != 0 ? x1 : x0, end ? z1 : z0,
                    up ? (end ? top1 : top0) : (end ? bottom1 : bottom0)));
        }
        return new Solid(name, role, material, corners);
    }

    /**
     * A block across from {@code x0} to {@code x1}, from {@code y0} up to {@code y1}, battered front and back: at its
     * foot reaching {@code foot} either way along from its middle, at its top {@code top}: a rampart, a gate's base.
     */
    public static Solid battered(final String name, final Role role, final Material material, final double x0,
                                 final double x1, final double foot, final double top, final double y0,
                                 final double y1) {
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean up = (k & 4) != 0;
            corners.add(new Point((k & 1) != 0 ? x1 : x0, ((k & 2) != 0 ? 1 : -1) * (up ? top : foot), up ? y1 : y0));
        }
        return new Solid(name, role, material, corners);
    }

    /**
     * A squared member {@code side} thick, its middle line from ({@code x0}, {@code y0}) to ({@code x1}, {@code y1})
     * across and up, at {@code z} along: a strut or a beam across a passage.
     */
    public static Solid strut(final String name, final Role role, final Material material, final double z,
                              final double x0, final double y0, final double x1, final double y1, final double side) {
        final double l = Math.hypot(x1 - x0, y1 - y0);
        final double nx = -(y1 - y0) / l * side / 2;
        final double ny = (x1 - x0) / l * side / 2;
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean end = (k & 1) != 0;
            final double s = (k & 4) != 0 ? 1 : -1;
            corners.add(new Point((end ? x1 : x0) + s * nx, z + ((k & 2) != 0 ? side / 2 : -side / 2),
                    (end ? y1 : y0) + s * ny));
        }
        return new Solid(name, role, material, corners);
    }

    /**
     * A block across from {@code x0} to {@code x1}, along from {@code z0} to {@code z1}, up from {@code y}, its top
     * sloping across: {@code top0} high at {@code x0}, {@code top1} at {@code x1}. A ramp.
     */
    public static Solid wedge(final String name, final Role role, final Material material, final double x0,
                              final double x1, final double z0, final double z1, final double y, final double top0,
                              final double top1) {
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean far = (k & 1) != 0;
            corners.add(new Point(far ? x1 : x0, (k & 2) != 0 ? z1 : z0, (k & 4) == 0 ? y : far ? top1 : top0));
        }
        return new Solid(name, role, material, corners);
    }
}

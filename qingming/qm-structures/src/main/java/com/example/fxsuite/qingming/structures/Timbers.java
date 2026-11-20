package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.ArrayList;
import java.util.List;

/**
 * Timbers and stones as solids ({@link Solid}), in a structure's own frame (across, along, up; centimetres): a squared
 * member from one point to another in a plane across, a block square to the frame, a board whose top follows a slope.
 */
final class Timbers {

    private Timbers() {
    }

    /**
     * A squared member {@code side} thick, its middle line from ({@code z0}, {@code y0}) to ({@code z1}, {@code y1})
     * along and up, at {@code x} across: its sides square to its line.
     */
    static Solid member(final String name, final Role role, final Material material, final double x, final double z0,
                        final double y0, final double z1, final double y1, final double side) {
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
    static Solid block(final String name, final Role role, final Material material, final double x0, final double x1,
                       final double z0, final double z1, final double y0, final double y1) {
        return board(name, role, material, x0, x1, z0, z1, y1, y1, y1 - y0);
    }

    /**
     * A board across from {@code x0} to {@code x1}, along from {@code z0} to {@code z1}, its top at {@code top0} at
     * the one end and {@code top1} at the other, {@code thick} under it: a deck's plank on a slope.
     */
    static Solid board(final String name, final Role role, final Material material, final double x0, final double x1,
                       final double z0, final double z1, final double top0, final double top1, final double thick) {
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final boolean end = (k & 2) != 0;
            final double top = end ? top1 : top0;
            corners.add(new Point((k & 1) != 0 ? x1 : x0, end ? z1 : z0, (k & 4) != 0 ? top : top - thick));
        }
        return new Solid(name, role, material, corners);
    }
}

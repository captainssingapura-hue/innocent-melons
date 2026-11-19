package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * What an office is furnished with, as solids and as what is in the way: boxes, tables on legs, chairs. In a building's
 * own frame: across, along (ahead, toward its door), up; centimetres; each standing on a floor so high.
 */
final class Furniture {

    /** How thick a table's top is, and a chair's seat; how square a table's legs are, a chair's half as square. */
    static final double TOP = 4;
    static final double LEG = 6;

    /** Which way whoever sits on a chair faces: its back on the other side. */
    enum Facing { AHEAD, BACK, RIGHT, LEFT }

    private Furniture() {
    }

    /** A box of {@code material}, square to the building, its corners in the order a solid's are: across, along, up. */
    static Solid box(final String name, final Material material, final double x0, final double x1, final double y0,
                     final double y1, final double z0, final double z1) {
        final List<Point> corners = new ArrayList<>(8);
        for (int k = 0; k < 8; k++) {
            corners.add(new Point((k & 1) == 0 ? x0 : x1, (k & 2) == 0 ? y0 : y1, (k & 4) == 0 ? z0 : z1));
        }
        return new Solid(name, Role.DRESSING, material, corners);
    }

    /**
     * A table {@code wide} across and {@code deep} along about ({@code x}, {@code y}), {@code high}, on a floor
     * {@code floor} high: its top of {@code top}, on four legs of {@code legs} at its corners.
     */
    static List<Solid> table(final String name, final Material top, final Material legs, final double x,
                             final double y, final double wide, final double deep, final double high,
                             final double floor) {
        final List<Solid> t = new ArrayList<>();
        final double x0 = x - wide / 2;
        final double x1 = x + wide / 2;
        final double y0 = y - deep / 2;
        final double y1 = y + deep / 2;
        t.add(box(name + ", top", top, x0, x1, y0, y1, high - TOP, high));
        for (final double lx : new double[] {x0, x1 - LEG}) {
            for (final double ly : new double[] {y0, y1 - LEG}) {
                t.add(box(name + ", leg", legs, lx, lx + LEG, ly, ly + LEG, floor, high - TOP));
            }
        }
        return t;
    }

    /**
     * A chair {@code size} square about ({@code x}, {@code y}), on a floor {@code floor} high, of {@code material}: its
     * seat {@code seat} high on four legs; its back, as high as {@code back}, behind whoever sits on it facing
     * {@code facing}.
     */
    static List<Solid> chair(final String name, final Material material, final double x, final double y,
                             final Facing facing, final double size, final double seat, final double back,
                             final double floor) {
        final List<Solid> c = new ArrayList<>();
        final double h = size / 2;
        final double leg = LEG / 2;
        c.add(box(name + ", seat", material, x - h, x + h, y - h, y + h, seat - TOP, seat));
        for (final double lx : new double[] {x - h, x + h - leg}) {
            for (final double ly : new double[] {y - h, y + h - leg}) {
                c.add(box(name + ", leg", material, lx, lx + leg, ly, ly + leg, floor, seat - TOP));
            }
        }
        c.add(switch (facing) {
            case BACK -> box(name + ", back", material, x - h, x + h, y + h - leg, y + h, seat, back);
            case AHEAD -> box(name + ", back", material, x - h, x + h, y - h, y - h + leg, seat, back);
            case LEFT -> box(name + ", back", material, x + h - leg, x + h, y - h, y + h, seat, back);
            case RIGHT -> box(name + ", back", material, x - h, x - h + leg, y - h, y + h, seat, back);
        });
        return c;
    }

    /** What is in the way of something {@code wide} across and {@code deep} along about ({@code x}, {@code y}): from {@code floor} to {@code top}. */
    static Prism footprint(final double x, final double y, final double wide, final double deep, final double floor,
                           final double top) {
        final double x0 = x - wide / 2;
        final double x1 = x + wide / 2;
        final double y0 = y - deep / 2;
        final double y1 = y + deep / 2;
        return new Prism(List.of(new Vec2(x0, y0), new Vec2(x1, y0), new Vec2(x1, y1), new Vec2(x0, y1)), floor, top);
    }
}

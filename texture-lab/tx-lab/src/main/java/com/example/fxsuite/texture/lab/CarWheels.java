package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A car's four wheels, where its skeleton says its axles end: each a plain drum of rubber, its tread and its two
 * sides, with a flat disc of steel on its outer side for the wheel itself. They are there so the car stands on
 * something and its wheelbase can be seen; a tyre has no tread pattern and a wheel no spokes.
 *
 * <p>Each is a sheet of the engine's, as the body's faces are: the tyres of the kind {@code tyre}, the discs of
 * the kind {@code wheel}. {@code round} is how big a wheel is from its middle to its tread, and {@code wide} how
 * wide its tyre is.
 */
public record CarWheels(Form skeleton, double round, double wide) {

    /** How many flat pieces a wheel goes round in. */
    private static final int ROUND = 28;
    /** How much of a wheel's size its steel disc is, and how far the disc stands out from the tyre's side. */
    private static final double DISC = 0.62;
    private static final double PROUD = 0.6;
    /** A wheel's well: how far inside the tyre its plate stands, and how much bigger round than the wheel it is. */
    private static final double WITHIN = 1.5;
    private static final double BEYOND = 14;

    /** The wheels of {@code body}, on its skeleton. */
    public CarWheels(final CarBody body) {
        this(body.skeleton(), body.wheel(), body.tyreWide());
    }

    /** The four tyres. */
    public List<Sheet> tyres() {
        final List<Sheet> tyres = new ArrayList<>();
        for (final Form.Vertex end : ends()) {
            final double out = Math.signum(end.across());
            tyres.add(tread(end));
            tyres.add(disc(end.name() + ", outer side", "tyre", end, end.across() + out * wide / 2, out, round));
            tyres.add(disc(end.name() + ", inner side", "tyre", end, end.across() - out * wide / 2, -out, round));
        }
        return tyres;
    }

    /** The four wheels' steel discs, each on the outer side of its tyre. */
    public List<Sheet> discs() {
        final List<Sheet> discs = new ArrayList<>();
        for (final Form.Vertex end : ends()) {
            final double out = Math.signum(end.across());
            discs.add(disc(end.name() + ", wheel", "wheel", end, end.across() + out * (wide / 2 + PROUD), out,
                    DISC * round));
        }
        return discs;
    }

    /**
     * What closes each wheel's well at its inner end: a flat round plate just inside the tyre, bigger than
     * the arch, with a face each way. With the well itself ({@link CarFaces#inside}) it shuts the wheel off: without
     * them the ground shows through the gap between a tyre and its arch,
     * from outside and, through the car, from inside. It belongs with the car's inside, and is as dark.
     */
    public List<Sheet> wells() {
        final List<Sheet> wells = new ArrayList<>();
        for (final Form.Vertex end : ends()) {
            final double out = Math.signum(end.across());
            final double across = out * within();
            wells.add(disc(end.name() + ", well, outer face", "well", end, across, out, round + BEYOND));
            wells.add(disc(end.name() + ", well, inner face", "well", end, across, -out, round + BEYOND));
        }
        return wells;
    }

    /** How far from the car's middle those plates stand: just inside the tyres. */
    public double within() {
        return Math.abs(ends().getFirst().across()) - wide / 2 - WITHIN;
    }

    /** The axles' ends: the middles of the wheels. */
    private List<Form.Vertex> ends() {
        return skeleton.vertices().stream().filter(vertex -> vertex.name().contains(" axle, ")).toList();
    }

    /** A wheel's tread: the band round it, facing out from its axle all the way round. */
    private Sheet tread(final Form.Vertex end) {
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> normals = new ArrayList<>();
        final List<int[]> triangles = new ArrayList<>();
        for (int piece = 0; piece < ROUND; piece++) {
            final Vec3 way = way(piece);
            for (final double side : new double[] {-wide / 2, wide / 2}) {
                points.add(new Vec3(end.across() + side, end.along() + round * way.y(), end.up() + round * way.z()));
                normals.add(way);
            }
            final int here = 2 * piece;
            final int next = 2 * ((piece + 1) % ROUND);
            triangles.add(new int[] {here, here + 1, next + 1});
            triangles.add(new int[] {here, next + 1, next});
        }
        return new Sheet(end.name() + ", tread", "tyre", points, triangles, normals);
    }

    /**
     * A flat round face about a wheel's axle: {@code across} the car, facing {@code facing} along the axle (-1 or
     * 1), as big round as {@code reach}.
     */
    private Sheet disc(final String name, final String kind, final Form.Vertex end, final double across,
                       final double facing, final double reach) {
        final List<Vec3> points = new ArrayList<>(List.of(new Vec3(across, end.along(), end.up())));
        final List<Vec3> normals = new ArrayList<>(List.of(new Vec3(facing, 0, 0)));
        final List<int[]> triangles = new ArrayList<>();
        for (int piece = 0; piece < ROUND; piece++) {
            final Vec3 way = way(piece);
            points.add(new Vec3(across, end.along() + reach * way.y(), end.up() + reach * way.z()));
            normals.add(new Vec3(facing, 0, 0));
            triangles.add(new int[] {0, 1 + piece, 1 + (piece + 1) % ROUND});
        }
        return new Sheet(name, kind, points, triangles, normals);
    }

    /** The way out from a wheel's axle, {@code piece} pieces of the way round it. */
    private Vec3 way(final int piece) {
        final double turn = 2 * Math.PI * piece / ROUND;
        return new Vec3(0, Math.cos(turn), Math.sin(turn));
    }
}

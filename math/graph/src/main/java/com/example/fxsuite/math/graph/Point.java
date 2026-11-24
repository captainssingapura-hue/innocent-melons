package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * Where a vertex of a graph drawn in space stands: on a plane, so far one way and so far the other (a map's spot), or
 * in space, so far across, up and along (a building's corner, a bone's landmark). A graph's vertices may be anything;
 * those that stand somewhere are points, all of one graph of as many dimensions as it is drawn in. Two points are
 * apart as far as the straight line between them; points of two different dimensions are never compared.
 */
public sealed interface Point extends Scalar permits Point.Planar, Point.Spatial {

    /** How many dimensions it stands in. */
    int dimensions();

    /** How far it is along {@code axis}, counted from 0. */
    double coordinate(int axis);

    /** How far it is from {@code other}, in a straight line; {@code other} in as many dimensions. */
    default double distance(final Point other) {
        if (other.dimensions() != dimensions()) {
            throw new IllegalArgumentException(this + " and " + other + " stand in different dimensions");
        }
        double sum = 0;
        for (int axis = 0; axis < dimensions(); axis++) {
            final double d = coordinate(axis) - other.coordinate(axis);
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    /**
     * A point on a plane.
     *
     * @param x how far one way
     * @param y how far the other
     */
    record Planar(double x, double y) implements Point {

        public Planar {
            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                throw new IllegalArgumentException("a point at " + x + ", " + y);
            }
        }

        @Override
        public int dimensions() {
            return 2;
        }

        @Override
        public double coordinate(final int axis) {
            return switch (axis) {
                case 0 -> x;
                case 1 -> y;
                default -> throw new IllegalArgumentException("a plane has no axis " + axis);
            };
        }
    }

    /**
     * A point in space; also a way and a distance from the origin, so points are moved by ways and lines between
     * them are ways.
     *
     * @param x how far across
     * @param y how far up
     * @param z how far along
     */
    record Spatial(double x, double y, double z) implements Point {

        public Spatial {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                throw new IllegalArgumentException("a point at " + x + ", " + y + ", " + z);
            }
        }

        @Override
        public int dimensions() {
            return 3;
        }

        @Override
        public double coordinate(final int axis) {
            return switch (axis) {
                case 0 -> x;
                case 1 -> y;
                case 2 -> z;
                default -> throw new IllegalArgumentException("space has no axis " + axis);
            };
        }

        /** It moved by {@code way}. */
        public Spatial plus(final Spatial way) {
            return new Spatial(x + way.x, y + way.y, z + way.z);
        }

        /** The way from {@code other} to it. */
        public Spatial minus(final Spatial other) {
            return new Spatial(x - other.x, y - other.y, z - other.z);
        }

        /** It {@code k} times as far from the origin. */
        public Spatial times(final double k) {
            return new Spatial(x * k, y * k, z * k);
        }

        /** How far it goes along {@code other}, times {@code other}'s length. */
        public double dot(final Spatial other) {
            return x * other.x + y * other.y + z * other.z;
        }

        /** The way square to it and to {@code other}, as long as the parallelogram they span is large. */
        public Spatial cross(final Spatial other) {
            return new Spatial(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
        }

        /** How far it is from the origin. */
        public double length() {
            return Math.sqrt(dot(this));
        }

        /** The same way, a unit long; there is no way to a point at the origin. */
        public Spatial unit() {
            final double length = length();
            if (!(length > 0)) {
                throw new IllegalArgumentException("no way to the origin itself");
            }
            return times(1 / length);
        }

        /** The point {@code t} of the way from it to {@code other}. */
        public Spatial toward(final Spatial other, final double t) {
            return plus(other.minus(this).times(t));
        }
    }

}

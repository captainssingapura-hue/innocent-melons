package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Point.Spatial;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How an edge of a form runs between its two landmarks, from its first to its second: what the edge carries.
 *
 * <ul>
 *   <li>{@link Straight}: the straight line.</li>
 *   <li>{@link Arc}: a circular arc through its two ends, its middle {@code rise} (a way, its length the arc's
 *       rise) off the middle of the straight line between them: a pointed arch's half, a half circle.</li>
 *   <li>{@link Bow}: bowed, its middle {@code bow} off the straight line, as full as {@code fullness} says: 2 a
 *       parabola, more keeping near the line until near its ends; or the part of a longer bow, from {@code t0} to
 *       {@code t1} of the way along it from {@code from} to {@code to}, so a long bowed line can be cut into
 *       edges.</li>
 *   <li>{@link Corner}: part of a quarter superellipse into the point {@code corner}, from {@code from} to
 *       {@code to}, as square as {@code squareness}, from {@code t0} to {@code t1} of the way along it.</li>
 * </ul>
 */
public sealed interface Course extends Scalar permits Course.Straight, Course.Arc, Course.Bow, Course.Corner {

    /** The point {@code s} of the way along it, 0 at {@code a}, 1 at {@code b}: the ends of the edge it runs. */
    Spatial at(Spatial a, Spatial b, double s);

    /** The same, {@code k} times as big about the origin: its points as far again from it, its rises as deep. */
    Course scaled(double k);

    /** Whether it runs straight. */
    default boolean straight() {
        return false;
    }

    /** The straight line. */
    record Straight() implements Course {

        @Override
        public Spatial at(final Spatial a, final Spatial b, final double s) {
            return a.toward(b, s);
        }

        @Override
        public Course scaled(final double k) {
            return this;
        }

        @Override
        public boolean straight() {
            return true;
        }
    }

    /** A circular arc, its middle {@code rise} off the straight line's middle. */
    record Arc(Spatial rise) implements Course {

        @Override
        public Spatial at(final Spatial a, final Spatial b, final double s) {
            final double height = rise.length();
            final double half = a.distance(b) / 2;
            if (height < 1e-9 || half < 1e-9) {
                return a.toward(b, s);
            }
            // the circle's radius, its centre on the far side of the line from the rise
            final double radius = (half * half + height * height) / (2 * height);
            final Spatial up = rise.times(1 / height);
            final Spatial across = b.minus(a).times(1 / (2 * half));
            final Spatial centre = a.toward(b, 0.5).plus(up.times(height - radius));
            // round from a to b over the top, in the plane of across and up, the angle falling
            final double start = Math.atan2(radius - height, -half);
            final double fromA = start < 0 ? start + 2 * Math.PI : start;
            final double toB = Math.atan2(radius - height, half);
            final double angle = fromA + s * (toB - fromA);
            return centre.plus(across.times(radius * Math.cos(angle))).plus(up.times(radius * Math.sin(angle)));
        }

        @Override
        public Course scaled(final double k) {
            return new Arc(rise.times(k));
        }
    }

    /**
     * A bow, its middle {@code bow} off the straight line, as full as {@code fullness}; the part of it from {@code t0}
     * to {@code t1} of the way from {@code from} to {@code to}, or, with both null, the whole between the edge's ends.
     */
    record Bow(Spatial bow, double fullness, Spatial from, Spatial to, double t0, double t1) implements Course {

        public Bow {
            if ((from == null) != (to == null)) {
                throw new IllegalArgumentException("a part of a bow runs from somewhere to somewhere");
            }
        }

        /** The whole bow between the edge's ends. */
        public Bow(final Spatial bow, final double fullness) {
            this(bow, fullness, null, null, 0, 1);
        }

        @Override
        public Spatial at(final Spatial a, final Spatial b, final double s) {
            if (from == null) {
                return bowed(a, b, s);
            }
            return bowed(from, to, t0 + s * (t1 - t0));
        }

        private Spatial bowed(final Spatial start, final Spatial end, final double t) {
            return start.toward(end, t).plus(bow.times(1 - Math.pow(Math.abs(2 * t - 1), fullness)));
        }

        @Override
        public Course scaled(final double k) {
            return new Bow(bow.times(k), fullness, from == null ? null : from.times(k), to == null ? null : to.times(k),
                    t0, t1);
        }
    }

    /** Part of a quarter superellipse into {@code corner}, from {@code from} to {@code to}, between {@code t0} and {@code t1}. */
    record Corner(Spatial corner, double squareness, Spatial from, Spatial to, double t0, double t1) implements Course {

        @Override
        public Spatial at(final Spatial a, final Spatial b, final double s) {
            final double angle = (t0 + s * (t1 - t0)) * Math.PI / 2;
            final double back = 1 - Math.pow(Math.sin(angle), 2 / squareness);
            final double on = 1 - Math.pow(Math.cos(angle), 2 / squareness);
            return corner.plus(from.minus(corner).times(back)).plus(to.minus(corner).times(on));
        }

        @Override
        public Course scaled(final double k) {
            return new Corner(corner.times(k), squareness, from.times(k), to.times(k), t0, t1);
        }
    }
}

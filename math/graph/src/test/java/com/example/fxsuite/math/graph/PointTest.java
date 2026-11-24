package com.example.fxsuite.math.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Points of a graph drawn in space: on a plane or in space, as far apart as the straight line between them, never
 * compared across dimensions, and never at a place that is no number.
 */
class PointTest {

    @Test
    void pointsAreAsFarApartAsTheLineBetweenThem() {
        assertEquals(5, new Point.Planar(0, 0).distance(new Point.Planar(3, 4)), 1e-12);
        assertEquals(3, new Point.Spatial(1, 2, 2).distance(new Point.Spatial(0, 0, 0)), 1e-12);
    }

    @Test
    void pointsOfDifferentDimensionsAreNeverCompared() {
        assertThrows(IllegalArgumentException.class,
                () -> new Point.Planar(0, 0).distance(new Point.Spatial(0, 0, 0)));
    }

    @Test
    void aPointStandsSomewhere() {
        assertThrows(IllegalArgumentException.class, () -> new Point.Planar(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> new Point.Spatial(0, Double.POSITIVE_INFINITY, 0));
    }

    @Test
    void waysInSpaceCrossSquareToBoth() {
        final Point.Spatial across = new Point.Spatial(1, 0, 0);
        final Point.Spatial up = new Point.Spatial(0, 1, 0);
        assertEquals(new Point.Spatial(0, 0, 1), across.cross(up));
        assertEquals(new Point.Spatial(0.5, 0.5, 0), across.toward(up, 0.5));
        assertThrows(IllegalArgumentException.class, () -> new Point.Spatial(0, 0, 0).unit());
    }
}

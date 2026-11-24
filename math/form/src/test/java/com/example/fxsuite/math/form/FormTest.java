package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Point.Spatial;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A form: its edges run through their landmarks, straight or as their courses curve; paths along them either way;
 * how long it is; what is wrong with it; scaled, the same shape bigger; faces only along its own edges, the surface
 * through one standing on its sides; and kept as text, read back the same.
 */
class FormTest {

    private static final double NEAR = 1e-9;

    /** A unit square on the ground, its far side bowed outward 0.25, its face spanned by its four sides. */
    private Form square() {
        return new FormBuilder("square")
                .landmark("a", 0, 0, 0).landmark("b", 1, 0, 0).landmark("c", 1, 0, 1).landmark("d", 0, 0, 1)
                .path("a", "b", "c").edge("c", "d", new Course.Bow(new Spatial(0, 0, 0.25), 2)).edge("d", "a")
                .face("floor", List.of(List.of("a", "b"), List.of("b", "c"), List.of("c", "d"), List.of("d", "a")))
                .made();
    }

    @Test
    void aCurvedEdgeRunsThroughItsEndsItsMiddleStandingOffTheLine() {
        final Form f = square();
        final List<Spatial> bowed = f.points(f.edge("c", "d").orElseThrow(), 0.01);
        assertEquals(f.at("c"), bowed.getFirst());
        assertEquals(f.at("d"), bowed.getLast());
        assertTrue(bowed.size() > 100, "pieces no longer than asked: " + bowed.size());
        final Spatial middle = new Course.Bow(new Spatial(0, 0, 0.25), 2).at(f.at("c"), f.at("d"), 0.5);
        assertEquals(1.25, middle.z(), NEAR);
        final Spatial arched = new Course.Arc(new Spatial(0, 1, 0))
                .at(new Spatial(-1, 0, 0), new Spatial(1, 0, 0), 0.5);
        assertEquals(1, arched.y(), NEAR);
        assertEquals(0, arched.x(), NEAR);
    }

    @Test
    void aStraightEdgeIsAsLongAsTheLineABowedOneLonger() {
        final Form f = square();
        assertEquals(1, f.length(f.edge("a", "b").orElseThrow()), NEAR);
        assertTrue(f.length(f.edge("c", "d").orElseThrow()) > 1.1);
        assertTrue(f.length() > 4.1);
    }

    @Test
    void aPathRunsAlongItsEdgesWhicheverWayTheyWereGiven() {
        final Form f = square();
        final Run back = f.run(List.of("a", "d", "c"), 0.05);
        assertEquals(f.at("a"), back.at(0));
        assertEquals(f.at("c"), back.at(1));
        assertTrue(back.length() > 2.1, "along the bow from d to c: " + back.length());
        assertThrows(IllegalArgumentException.class, () -> f.run(List.of("a", "c"), 0.05));
    }

    @Test
    void itSaysWhatIsWrongWithIt() {
        final Form f = new FormBuilder("wrong").landmark("a", 0, 0, 0).landmark("b", 0.05, 0, 0).landmark("c", 5, 0, 0)
                .landmark("lone", 9, 9, 9).edge("a", "c").edge("c", "a").edge("b", "c").made();
        final List<String> problems = f.problems();
        assertTrue(problems.contains("a and b are one point"), problems.toString());
        assertTrue(problems.contains("an edge twice, c to a"), problems.toString());
        assertTrue(problems.contains("lone joins nothing"), problems.toString());
        assertEquals(2, f.pieces());
        assertTrue(square().problems().isEmpty());
    }

    @Test
    void scaledItIsTheSameShapeBigger() {
        final Form f = square();
        final Form big = f.scaled(2);
        assertEquals(2 * f.length(), big.length(), 1e-6);
        assertEquals(new Spatial(2, 0, 2), big.at("c"));
        assertEquals(f.faces(), big.faces());
    }

    @Test
    void aFaceRunsOnlyAlongItsFormsEdgesItsSidesEndToEnd() {
        assertThrows(IllegalArgumentException.class, () -> new Face("open",
                List.of(List.of("a", "b"), List.of("c", "d"), List.of("d", "a"), List.of("a"))));
        final FormBuilder b = new FormBuilder("x").landmark("a", 0, 0, 0).landmark("b", 1, 0, 0)
                .landmark("c", 1, 1, 0).path("a", "b", "c");
        b.face("no edge back", List.of(List.of("a", "b"), List.of("b", "c"), List.of("c", "a"), List.of("a")));
        assertThrows(IllegalArgumentException.class, b::made);
    }

    @Test
    void theSurfaceThroughAFaceStandsOnItsCornersAndRunsAlongItsSides() {
        final Form f = square();
        final Patch p = new Patch(f, f.faces().getFirst(), 0.01);
        assertEquals(0, p.at(0, 0).distance(f.at("a")), NEAR);
        assertEquals(0, p.at(1, 0).distance(f.at("b")), NEAR);
        assertEquals(0, p.at(1, 1).distance(f.at("c")), NEAR);
        assertEquals(0, p.at(0, 1).distance(f.at("d")), NEAR);
        assertEquals(1.25, p.at(0.5, 1).z(), 1e-3);
        assertEquals(0.5 + 0.125, p.at(0.5, 0.5).z(), 1e-3);
        assertEquals(1, Math.abs(p.facing(0.5, 0.5).y()), 1e-6);
    }

    @Test
    void aThreeSidedFaceClosesToAPoint() {
        final Form f = new FormBuilder("tri").landmark("a", 0, 0, 0).landmark("b", 2, 0, 0).landmark("c", 0, 0, 2)
                .path("a", "b", "c", "a")
                .face("tri", List.of(List.of("a", "b"), List.of("b", "c"), List.of("c"), List.of("c", "a"))).made();
        final Patch p = new Patch(f, f.faces().getFirst(), 0.1);
        assertEquals(0, p.at(0.3, 1).distance(f.at("c")), NEAR);
        assertEquals(0, p.at(0.8, 1).distance(f.at("c")), NEAR);
        assertTrue(p.at(0.5, 0.5).x() + p.at(0.5, 0.5).z() <= 2 + 1e-9, "inside the triangle");
    }

    @Test
    void keptAsTextItReadsBackTheSame() {
        final FormText text = new FormText();
        final Form f = square();
        assertEquals(f, text.read(text.written(f)));
        final Form read = text.read("# a comment\nform \"bone\"\nlandmark \"top end\" 1 2 3\nlandmark \"foot\" 1 -2 3\n"
                + "edge \"top end\" \"foot\" arc 0.5 0 0\nshade \"for whatever uses it\"\n");
        assertEquals("bone", read.name());
        assertEquals(new Spatial(1, 2, 3), read.at("top end"));
        assertTrue(read.length() > 4);
    }
}

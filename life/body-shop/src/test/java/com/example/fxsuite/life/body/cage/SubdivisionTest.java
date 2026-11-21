package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A cage smoothed: a cube's, a step at a time, four times the faces each step and a point for each point, edge and
 * face; rounder each step, toward a ball inside the cube; an edge creased kept straighter than its fellows; and fitted,
 * its surface through its points as drawn.
 */
class SubdivisionTest {

    private final Subdivision s = Subdivision.INSTANCE;

    /** A cube 2 cm a side round the origin, its faces facing out; its top front edge creased {@code sharp}. */
    private Cage cube(final double sharp) {
        final List<V3> p = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            p.add(new V3((i & 1) == 0 ? -1 : 1, (i & 2) == 0 ? -1 : 1, (i & 4) == 0 ? -1 : 1));
            names.add("corner " + i);
        }
        final List<int[]> f = List.of(new int[] {0, 2, 3, 1}, new int[] {4, 5, 7, 6}, new int[] {0, 1, 5, 4},
                new int[] {2, 6, 7, 3}, new int[] {0, 4, 6, 2}, new int[] {1, 3, 7, 5});
        return new Cage(p, names, f, List.of(new Crease("the top front edge", List.of(6, 7), sharp, false)));
    }

    @Test
    void eachStepFourTimesTheFacesAndAPointForEveryPointEdgeAndFace() {
        final Subdivision.Level once = s.once(s.of(cube(0), 1));
        assertEquals(24, once.faces().size());
        assertEquals(8 + 12 + 6, once.size());
        final Subdivision.Level thrice = s.times(once, 2);
        assertEquals(24 * 16, thrice.faces().size());
        assertTrue(volume(s.mesh(thrice).points(), s.mesh(thrice).triangles()) > 0, "facing out");
    }

    @Test
    void itRoundsTowardABallInsideTheCube() {
        final Subdivision.Level l = s.times(s.of(cube(0), 1), 4);
        double least = Double.MAX_VALUE;
        double most = 0;
        for (int i = 0; i < l.size(); i++) {
            least = Math.min(least, l.point(i).length());
            most = Math.max(most, l.point(i).length());
        }
        assertTrue(most < Math.sqrt(3) && least > 0.5 && most / least < 1.4, least + " to " + most);
    }

    @Test
    void aCreasedEdgeIsKeptStraighterThanItsFellows() {
        // the creased edge's middle, its points' after one step, against an edge not creased
        final double creased = s.times(s.of(cube(3), 1), 4).point(edgeMiddle(cube(3), 6, 7)).length();
        final double smooth = s.times(s.of(cube(0), 1), 4).point(edgeMiddle(cube(0), 6, 7)).length();
        assertTrue(creased > smooth + 0.1, creased + " creased, " + smooth + " smooth");
    }

    @Test
    void aFittedCagesSurfacePassesThroughItsPointsAsDrawn() {
        final Cage drawn = cube(0);
        final Cage fitted = Fitting.INSTANCE.fitted(drawn, 1);
        final double before = Fitting.INSTANCE.miss(drawn, drawn, 1);
        final double after = Fitting.INSTANCE.miss(fitted, drawn, 1);
        assertTrue(after < before / 4, before + " before, " + after + " after");
    }

    /** The number, after one step, of the point made for the edge from {@code a} to {@code b}. */
    private int edgeMiddle(final Cage c, final int a, final int b) {
        final Subdivision.Level l = s.once(s.of(c, 1));
        final V3 mid = c.points().get(a).plus(c.points().get(b)).times(0.5);
        int best = 0;
        for (int i = c.size(); i < l.size(); i++) {
            if (l.point(i).minus(mid).length() < l.point(best).minus(mid).length()) {
                best = i;
            }
        }
        return best;
    }

    private double volume(final float[] p, final int[] t) {
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            v += a.dot(b.cross(c)) / 6;
        }
        return v;
    }
}

package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoftSolveTest {

    private static final SoftRegion ROOM = new SoftRegion(new V3(-10, -10, -10), new V3(10, 10, 10));
    private static final double CELL = 0.5;
    /** How finely each face of a box is cut, each way. */
    private static final int FINE = 8;

    /** Nothing to keep out of. */
    private final Walls none = new Walls() {
        @Override
        public double distance(final double x, final double y, final double z) {
            return 100;
        }

        @Override
        public V3 out(final double x, final double y, final double z) {
            return V3.ZERO;
        }
    };

    /** A floor where height is nothing, kept above. */
    private final Walls floor = new Walls() {
        @Override
        public double distance(final double x, final double y, final double z) {
            return y;
        }

        @Override
        public V3 out(final double x, final double y, final double z) {
            return new V3(0, 1, 0);
        }
    };

    /** Fibres running across, the way the first number grows. */
    private final Along across = p -> new V3(1, 0, 0);

    /** A muscle: a box from {@code lo} to {@code hi}, each face finely cut, facing out; its elements. */
    private record Box(Mesher.Mesh surface, Lattice lattice) {
    }

    private Box box(final V3 lo, final V3 hi) {
        final List<Float> points = new ArrayList<>();
        final List<Integer> triangles = new ArrayList<>();
        final double[] l = {lo.x(), lo.y(), lo.z()};
        final double[] h = {hi.x(), hi.y(), hi.z()};
        for (int axis = 0; axis < 3; axis++) {
            for (final int side : new int[] {0, 1}) {
                final int u = (axis + 1) % 3;
                final int v = (axis + 2) % 3;
                final int first = points.size() / 3;
                for (int i = 0; i <= FINE; i++) {
                    for (int j = 0; j <= FINE; j++) {
                        final double[] p = new double[3];
                        p[axis] = side == 0 ? l[axis] : h[axis];
                        p[u] = l[u] + (h[u] - l[u]) * i / FINE;
                        p[v] = l[v] + (h[v] - l[v]) * j / FINE;
                        points.add((float) p[0]);
                        points.add((float) p[1]);
                        points.add((float) p[2]);
                    }
                }
                for (int i = 0; i < FINE; i++) {
                    for (int j = 0; j < FINE; j++) {
                        final int a = first + i * (FINE + 1) + j;
                        final int b = a + FINE + 1;
                        // u then v goes round the way out of the high side
                        final int[][] tri = side == 1 ? new int[][] {{a, b, b + 1}, {a, b + 1, a + 1}}
                                : new int[][] {{a, b + 1, b}, {a, a + 1, b + 1}};
                        for (final int[] t : tri) {
                            triangles.add(t[0]);
                            triangles.add(t[1]);
                            triangles.add(t[2]);
                        }
                    }
                }
            }
        }
        final float[] p = new float[points.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = points.get(i);
        }
        final Mesher.Mesh surface = new Mesher.Mesh(p, triangles.stream().mapToInt(Integer::intValue).toArray());
        return new Box(surface, LatticeMaker.INSTANCE.of(ROOM, surface, CELL));
    }

    private SoftSolve.Settings settings(final double least, final int drawIn, final int grow, final int settle) {
        return settings(least, drawIn, grow, settle, 0);
    }

    private SoftSolve.Settings settings(final double least, final int drawIn, final int grow, final int settle,
                                        final double tension) {
        return new SoftSolve.Settings(10, 0.45, least, drawIn, grow, settle, 2, 1, 0.02, 0.05, 0.75, tension, false);
    }

    private SoftSolve solve(final List<Box> boxes, final List<List<V3>> holds, final List<Along> fibres,
                            final Walls walls, final SoftSolve.Settings s) {
        return new SoftSolve(ROOM, boxes.stream().map(Box::lattice).toList(),
                boxes.stream().map(Box::surface).toList(), holds, fibres, walls, s);
    }

    private SoftSolve.Moment solved(final SoftSolve s) {
        SoftSolve.Moment m = null;
        while (!s.done()) {
            m = s.stepped();
        }
        return m;
    }

    /** Box {@code k}'s surface as {@code s} has carried it, three numbers a point. */
    private float[] surface(final SoftSolve s, final List<Box> boxes, final int k) {
        return s.lattices().get(k).carrying(boxes.get(k).surface().points());
    }

    @Test
    void aMuscleAtRestStaysWhereItIs() {
        final Box b = box(new V3(0.1, 0.1, 0.1), new V3(1.9, 1.9, 1.9));
        final SoftSolve s = solve(List.of(b), List.of(List.of()), List.of(across), none, settings(1, 0, 0, 5));
        final SoftSolve.Moment m = solved(s);
        final Lattice now = s.lattices().getFirst();
        for (int i = 0; i < b.lattice().nodes().length; i++) {
            assertEquals(b.lattice().nodes()[i], now.nodes()[i], 1e-4, "number " + i);
        }
        assertEquals(1, m.room(), 1e-6);
    }

    @Test
    void drawnInItShrinksAndGrownItComesBack() {
        final Box b = box(new V3(0.1, 0.1, 0.1), new V3(1.9, 1.9, 1.9));
        final SoftSolve.Moment drawn = solved(solve(List.of(b), List.of(List.of()), List.of(across), none,
                settings(0.5, 200, 0, 0)));
        assertTrue(drawn.room() < 0.3, "drawn in to an eighth or so: " + drawn.room());
        final SoftSolve both = solve(List.of(b), List.of(List.of()), List.of(across), none,
                settings(0.5, 200, 200, 200));
        assertEquals(1, solved(both).room(), 0.05, "grown back");
        assertEquals(0, both.inverted());
    }

    @Test
    void twoMusclesOverlappingAtFirstEndSideBySideTheirSurfacesTouching() {
        final List<Box> boxes = List.of(box(new V3(0.1, 0.1, 0.1), new V3(2.9, 1.9, 1.9)),
                box(new V3(1.6, 0.1, 0.1), new V3(4.4, 1.9, 1.9)));
        final SoftSolve s = solve(boxes, List.of(List.of(), List.of()), List.of(across, across), none,
                settings(0.35, 100, 300, 200));
        solved(s);
        double mostOfA = -1e9;
        final float[] a = surface(s, boxes, 0);
        for (int i = 0; i < a.length; i += 3) {
            mostOfA = Math.max(mostOfA, a[i]);
        }
        double leastOfB = 1e9;
        final float[] b = surface(s, boxes, 1);
        for (int i = 0; i < b.length; i += 3) {
            leastOfB = Math.min(leastOfB, b[i]);
        }
        assertTrue(mostOfA <= leastOfB + 0.1, "side by side: " + mostOfA + " and " + leastOfB);
        assertTrue(leastOfB - mostOfA < 0.3, "with next to nothing between: " + (leastOfB - mostOfA));
        assertEquals(0, s.inverted());
    }

    @Test
    void itIsKeptOutOfTheBones() {
        final List<Box> boxes = List.of(box(new V3(0.1, -0.9, 0.1), new V3(1.9, 1.4, 1.9)));
        final SoftSolve s = solve(boxes, List.of(List.of()), List.of(across), floor, settings(0.5, 50, 150, 150));
        solved(s);
        final float[] p = surface(s, boxes, 0);
        for (int i = 0; i < p.length; i += 3) {
            assertTrue(p[i + 1] > -0.02, "point " + i / 3 + " above the floor: " + p[i + 1]);
        }
    }

    @Test
    void whereItIsHeldItStays() {
        final Box b = box(new V3(0.1, 0.1, 0.1), new V3(1.9, 1.9, 1.9));
        final V3 hold = new V3(0, 0, 0);
        final SoftSolve s = solve(List.of(b), List.of(List.of(hold)), List.of(across), none,
                settings(0.5, 100, 0, 0));
        solved(s);
        boolean found = false;
        for (int i = 0; i < b.lattice().nodeCount(); i++) {
            if (b.lattice().node(i).minus(hold).length() <= 0.75) {
                found = true;
                assertEquals(0, s.lattices().getFirst().node(i).minus(b.lattice().node(i)).length(), 1e-6);
            }
        }
        assertTrue(found);
    }

    @Test
    void wrappedInFasciaTwoMusclesApartArePressedTogether() {
        final SoftRegion slab = new SoftRegion(new V3(-5, -1, -5), new V3(10, 3, 5));
        final List<Box> boxes = List.of(box(new V3(0.1, 0.1, 0.1), new V3(1.9, 1.9, 1.9)),
                box(new V3(2.6, 0.1, 0.1), new V3(4.4, 1.9, 1.9)));
        final List<Box> inSlab = boxes.stream().map(b -> new Box(b.surface(),
                LatticeMaker.INSTANCE.of(slab, b.surface(), CELL))).toList();
        final double before = gap(inSlab, null);
        final SoftSolve loose = new SoftSolve(slab, inSlab.stream().map(Box::lattice).toList(),
                inSlab.stream().map(Box::surface).toList(), List.of(List.of(), List.of()), List.of(across, across),
                none, new SoftSolve.Settings(10, 0.45, 1, 0, 0, 300, 2, 1, 0.02, 0.05, 0.75, 0, false));
        solved(loose);
        assertEquals(before, gap(inSlab, loose), 0.05, "unwrapped, they stay apart");
        final SoftSolve wrapped = new SoftSolve(slab, inSlab.stream().map(Box::lattice).toList(),
                inSlab.stream().map(Box::surface).toList(), List.of(List.of(), List.of()), List.of(across, across),
                none, new SoftSolve.Settings(10, 0.45, 1, 0, 0, 300, 2, 1, 0.02, 0.05, 0.75, 0, true));
        final SoftSolve.Moment m = solved(wrapped);
        final double after = gap(inSlab, wrapped);
        assertTrue(after < before - 0.3, "wrapped, pressed together: " + before + " to " + after);
        final double shared = shared(inSlab, wrapped);
        assertTrue(shared < 0.05 * 1.8 * 1.8 * 1.8, "and not into each other: " + shared + " cm3 shared");
        assertTrue(m.sleeve() > 0, "the sleeve holds them");
        assertEquals(0, wrapped.inverted());
    }

    /** How much room the two boxes share, cm3, as {@code s} has carried them, found on a grid 0.1 apart. */
    private double shared(final List<Box> boxes, final SoftSolve s) {
        final double cell = 0.1;
        final double[] lo = {-1, -1, -1};
        final int[] n = {70, 40, 40};
        final boolean[] a = com.example.fxsuite.life.body.head.Crossings.INSTANCE.inside(
                new Mesher.Mesh(surface(s, boxes, 0), boxes.get(0).surface().triangles()), lo, cell, n);
        final boolean[] b = com.example.fxsuite.life.body.head.Crossings.INSTANCE.inside(
                new Mesher.Mesh(surface(s, boxes, 1), boxes.get(1).surface().triangles()), lo, cell, n);
        int both = 0;
        for (int i = 0; i < a.length; i++) {
            both += a[i] && b[i] ? 1 : 0;
        }
        return both * cell * cell * cell;
    }

    /** How far apart the first box's surface and the second's are across, as {@code s} has them, or as at first. */
    private double gap(final List<Box> boxes, final SoftSolve s) {
        double most = -1e9;
        double least = 1e9;
        final float[] a = s == null ? boxes.get(0).surface().points() : surface(s, boxes, 0);
        final float[] b = s == null ? boxes.get(1).surface().points() : surface(s, boxes, 1);
        for (int i = 0; i < a.length; i += 3) {
            most = Math.max(most, a[i]);
        }
        for (int i = 0; i < b.length; i += 3) {
            least = Math.min(least, b[i]);
        }
        return least - most;
    }

    /** Fibres along the strip {@link #arched} makes, arched up {@code high}. */
    private Along alongArch(final double high) {
        return p -> {
            final double slope = high * Math.PI / 6 * Math.cos(Math.PI * p.x() / 6);
            return new V3(1, slope, 0).times(1 / Math.sqrt(1 + slope * slope));
        };
    }

    /** A strip from nought to six across, arched up {@code high} in its middle. */
    private Box arched(final double high) {
        final Box flat = box(new V3(0.1, 1.1, 0.1), new V3(5.9, 1.9, 0.9));
        final float[] n = flat.lattice().nodes().clone();
        for (int i = 0; i < n.length; i += 3) {
            n[i + 1] += (float) (high * Math.sin(Math.PI * n[i] / 6));
        }
        final Lattice l = flat.lattice();
        return new Box(flat.surface(), new Lattice(l.cell(), n, l.tets(), l.fixed(), l.holder(), l.weights()));
    }

    private double middle(final Lattice l) {
        double low = 1e9;
        for (int i = 0; i < l.nodeCount(); i++) {
            if (Math.abs(l.node(i).x() - 3) < 0.3) {
                low = Math.min(low, l.node(i).y());
            }
        }
        return low;
    }

    @Test
    void heldAtItsEndsATautMusclePullsItselfDownOntoWhatIsUnderIt() {
        final Box b = arched(1.0);
        final List<V3> ends = List.of(new V3(0, 1.5, 0.5), new V3(6, 1.5, 0.5));
        final double before = middle(b.lattice());
        final SoftSolve slack = solve(List.of(b), List.of(ends), List.of(alongArch(1.0)), floor,
                settings(1, 0, 0, 300, 0));
        solved(slack);
        assertEquals(before, middle(slack.lattices().getFirst()), 0.05, "slack, it stays as it is");
        final SoftSolve taut = solve(List.of(b), List.of(ends), List.of(alongArch(1.0)), floor,
                settings(1, 0, 0, 300, 0.15));
        final SoftSolve.Moment m = solved(taut);
        assertEquals(1, m.room(), 0.04, "its room kept, as near as a material of its Poisson's ratio keeps it");
        final double after = middle(taut.lattices().getFirst());
        assertTrue(after < before - 0.2, "taut, it is pulled down: " + before + " to " + after);
    }
}

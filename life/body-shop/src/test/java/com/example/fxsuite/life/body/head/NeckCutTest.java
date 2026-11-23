package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A surface cut, the pieces the neck is joined from: what is kept of a ball cut through its middle all on the side kept,
 * ending on the cut itself, its edge all the way round it; and which points are inside a ball left open at its foot,
 * told by lines up, the opening letting none of them out, as if shut there.
 */
class NeckCutTest {

    private final Mesher.Mesh ball = ball();

    private Mesher.Mesh ball() {
        final Sketch s = new Sketch();
        s.ball(V3.ZERO, 5);
        return Mesher.of(new Field(s.done().elements()), 0.5);
    }

    private V3[] points(final Mesher.Mesh m) {
        final V3[] w = new V3[m.points().length / 3];
        for (int i = 0; i < w.length; i++) {
            w[i] = new V3(m.points()[3 * i], m.points()[3 * i + 1], m.points()[3 * i + 2]);
        }
        return w;
    }

    @Test
    void whatIsKeptEndsOnTheCutAllTheWayRound() {
        final V3[] w = points(ball);
        final double[] off = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            off[i] = w[i].y();
        }
        final NeckCut.Cut cut = NeckCut.INSTANCE.cut(ball.triangles(), w, off);
        for (final int[] t : cut.kept()) {
            for (final int i : t) {
                assertTrue(cut.where()[i].y() <= 1e-9, "kept below the cut");
            }
        }
        final NeckEdge.Edge edge = NeckEdge.INSTANCE.of(cut.kept(), cut.where(), V3::y);
        double round = 0;
        for (int k = 0; k < edge.where().length; k++) {
            assertEquals(0, edge.where()[k].y(), 1e-9, "on the cut");
            round += edge.where()[k].minus(edge.where()[(k + 1) % edge.where().length]).length();
        }
        assertEquals(2 * Math.PI * 5, round, 0.05 * 2 * Math.PI * 5, "all the way round");
    }

    @Test
    void insideAnOpenBallIsToldByLinesUp() {
        final V3[] w = points(ball);
        final List<Integer> kept = new ArrayList<>();
        final int[] t = ball.triangles();
        for (int i = 0; i < t.length; i += 3) {
            if (w[t[i]].y() > -4 || w[t[i + 1]].y() > -4 || w[t[i + 2]].y() > -4) {
                kept.add(t[i]);
                kept.add(t[i + 1]);
                kept.add(t[i + 2]);
            }
        }
        final Mesher.Mesh open = new Mesher.Mesh(ball.points(), kept.stream().mapToInt(Integer::intValue).toArray());
        final int[] n = {15, 15, 15};
        final boolean[] in = Crossings.INSTANCE.inside(open, new double[] {-7, -7, -7}, 1, n);
        assertTrue(in[at(0, 0, 0)], "its middle");
        assertTrue(in[at(0, -3, 0)], "low in it, above the opening");
        assertTrue(in[at(3, 2, 0)], "toward its side");
        assertFalse(in[at(0, 6, 0)], "above it");
        assertFalse(in[at(6, 0, 0)], "beside it");
        assertTrue(in[at(0, -6, 0)], "straight under its opening, as if shut there");
        assertFalse(in[at(6, -6, 0)], "below it, off to the side");
    }

    private int at(final int x, final int y, final int z) {
        return ((z + 7) * 15 + y + 7) * 15 + x + 7;
    }
}

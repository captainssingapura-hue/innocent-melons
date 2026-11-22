package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A blank fitted to a ball with a bump on its front, the bump masked as its nose: the ball's shape taken, the bump
 * left out, the blank as round there as the ball.
 */
class BlankFitTest {

    /** How big the ball is round, its middle's height, cm. */
    private static final double BALL = 9;
    private static final double MIDDLE = 4;

    @Test
    void itTakesTheShapeButNotWhatIsMasked() {
        final List<Float> at = new ArrayList<>();
        final List<Integer> t = new ArrayList<>();
        final List<Boolean> bumped = new ArrayList<>();
        final int rows = 40;
        final int cols = 48;
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < cols; j++) {
                final double a = Math.PI * i / rows;
                final double b = 2 * Math.PI * j / cols;
                V3 p = new V3(Math.sin(a) * Math.sin(b), -Math.cos(a), Math.sin(a) * Math.cos(b)).times(BALL);
                final boolean bump = p.z() > 7.5 && Math.abs(p.x()) < 2.5 && Math.abs(p.y()) < 2.5;
                bumped.add(bump);
                if (bump) {
                    p = p.times(1.25);
                }
                at.addAll(List.of((float) p.x(), (float) (p.y() + MIDDLE), (float) p.z()));
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final int a = i * cols + j;
                final int b = i * cols + (j + 1) % cols;
                t.addAll(List.of(a, a + cols, b, b, a + cols, b + cols));
            }
        }
        final float[] p = new float[at.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = at.get(i);
        }
        final int[] tri = t.stream().mapToInt(Integer::intValue).toArray();
        final Feature[] masked = new Feature[tri.length / 3];
        for (int k = 0; k < masked.length; k++) {
            final boolean on = bumped.get(tri[3 * k]) || bumped.get(tri[3 * k + 1]) || bumped.get(tri[3 * k + 2]);
            masked[k] = on ? Feature.NOSE : null;
        }
        final ReferenceHead ball = new ReferenceHead(new Mesher.Mesh(p, tri), "a ball", new V3(3, MIDDLE, 8),
                new V3(-3, MIDDLE, 8), new V3(0, MIDDLE - 4, 8));
        final Mesher.Mesh blank = BlankFit.INSTANCE.of(ball, masked);
        double ahead = 0;
        double out = 0;
        for (int i = 0; i < blank.size(); i++) {
            final V3 q = new V3(blank.points()[3 * i], blank.points()[3 * i + 1] - MIDDLE, blank.points()[3 * i + 2]);
            if (Math.abs(q.x()) < 1 && Math.abs(q.y()) < 1) {
                ahead = Math.max(ahead, q.z());
            }
            if (Math.abs(q.y()) < 1 && Math.abs(q.z()) < 1) {
                out = Math.max(out, q.x());
            }
        }
        assertEquals(BALL, out, 0.5, "the ball's side taken");
        assertTrue(ahead < BALL + 0.3, "the bump, " + BALL * 0.25 + " cm out, left out: its front " + ahead);
    }
}

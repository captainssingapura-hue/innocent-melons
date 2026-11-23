package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A ball's surface of triangles made a solid: inside it less than nothing, outside it more, on it nothing, near enough;
 * a piece of it made a solid within a box, its inside where another solid says.
 */
class MeshSolidTest {

    /** How big the ball is round, cm. */
    private static final double BALL = 5;

    @Test
    void itsInsideIsInsideAndItsSurfaceOnIt() {
        final Field.Sampled s = MeshSolid.INSTANCE.closed(ball(), 0.25);
        assertTrue(s.at(0, 0, 0) < 0, "its middle inside");
        assertTrue(s.at(0, 0, BALL + 1) > 0, "a point beyond it outside");
        assertEquals(0, s.at(BALL, 0, 0), 0.1, "its surface");
        assertEquals(-0.5, s.at(0, BALL - 0.5, 0), 0.1, "half a centimetre in, half a centimetre in");
    }

    @Test
    void aPieceOfItIsInsideWhereItsBlankIs() {
        final Field.Sampled whole = MeshSolid.INSTANCE.closed(ball(), 0.25);
        final Field.Sampled piece = MeshSolid.INSTANCE.within(ball(), new double[] {-2, -2, 2, 2, 2, 7}, 0.2,
                whole);
        assertEquals(0, piece.at(0, 0, BALL), 0.1, "its surface");
        assertTrue(piece.at(0, 0, 2.5) < 0, "deep inside, as its blank is");
        assertTrue(piece.at(0, 0, 6.8) > 0, "well outside");
    }

    /** A ball of triangles, about the middle, going round to face out. */
    private Mesher.Mesh ball() {
        final List<Float> at = new ArrayList<>();
        final List<Integer> t = new ArrayList<>();
        final int rows = 30;
        final int cols = 36;
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < cols; j++) {
                final double a = Math.PI * i / rows;
                final double b = 2 * Math.PI * j / cols;
                at.addAll(List.of((float) (BALL * Math.sin(a) * Math.sin(b)), (float) (-BALL * Math.cos(a)),
                        (float) (BALL * Math.sin(a) * Math.cos(b))));
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
        return new Mesher.Mesh(p, t.stream().mapToInt(Integer::intValue).toArray());
    }
}

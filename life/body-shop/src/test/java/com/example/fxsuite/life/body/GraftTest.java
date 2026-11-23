package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A ball with a bigger ball grafted in about a point on its side: within the graft's region the bigger ball, outside it
 * the ball, between the one going over into the other.
 */
class GraftTest {

    private final Field.Shape ball = ball(V3.ZERO, 5);
    private final Graft graft = new Graft(ball, ball(V3.ZERO, 7), ball(new V3(0, 0, 6), 2), 1);

    @Test
    void withinItsRegionThePiece() {
        assertEquals(-1, graft.at(0, 0, 6), 1e-9, "a centimetre inside the bigger ball");
    }

    @Test
    void outsideItsRegionTheShape() {
        assertEquals(4, graft.at(0, 0, 9), 1e-9, "four centimetres outside the ball");
        assertEquals(-5, graft.at(0, 0, 0), 1e-9, "the ball's middle, as deep in it as it is round");
    }

    @Test
    void betweenTheOneGoingOverIntoTheOther() {
        assertEquals(1.5, graft.at(0, 0, 7.5), 1e-9, "halfway in from the region's side, halfway between the two");
    }

    private Field.Shape ball(final V3 c, final double r) {
        return Field.Ellipsoid.of(c, new V3(r, 0, 0), new V3(0, r, 0), new V3(0, 0, r));
    }
}

package com.example.fxsuite.life.body.hand;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A mechanical hand: a palm moving with the hand, and on every segment a link in the paint asked and a barrel in the
 * housings'. Each link lying along its segment, from its joint to short of the next, no fatter than its finger; each
 * barrel at its segment's joint.
 */
class MechanicalHandTest {

    private final List<MechanicalHand.Piece> pieces = new MechanicalHand().of(Paint.SHELL);

    @Test
    void aPalmAndOnEverySegmentALinkAndABarrelEachWhereItsSegmentIs() {
        assertEquals(1, pieces.stream().filter(p -> p.segment().isEmpty()).count(), "one palm");
        for (final Segment s : Segment.values()) {
            final List<MechanicalHand.Piece> mine = pieces.stream().filter(p -> p.segment().equals(Optional.of(s)))
                    .toList();
            assertEquals(List.of(Paint.SHELL, Paint.HOUSING), mine.stream().map(MechanicalHand.Piece::paint).toList());
            final double length = s.end().minus(s.joint()).length();
            final double fat = 1.1 * s.digit().thick();
            for (final V3 p : points(mine.get(0))) {
                final double along = p.minus(s.joint()).dot(s.way());
                assertTrue(along > -fat && along < length + fat, s + "'s link along it: " + along);
                assertTrue(p.minus(s.joint().plus(s.way().times(along))).length() < fat, s + "'s link no fatter");
            }
            for (final V3 p : points(mine.get(1))) {
                assertTrue(p.minus(s.joint()).length() < fat, s + "'s barrel at its joint: " + p);
            }
        }
    }

    /** {@code piece}'s points. */
    private List<V3> points(final MechanicalHand.Piece piece) {
        final float[] p = piece.mesh().points();
        assertTrue(p.length > 0, "something made");
        return java.util.stream.IntStream.range(0, p.length / 3).mapToObj(i -> new V3(p[3 * i], p[3 * i + 1],
                p[3 * i + 2])).toList();
    }
}

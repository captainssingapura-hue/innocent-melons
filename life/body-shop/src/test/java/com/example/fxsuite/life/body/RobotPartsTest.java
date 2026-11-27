package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A robot's frame on the male model as bound: something on every part; its own waist, chest, neck and head, its
 * visor lit; a housing at each limb's joint; its left a mirror of its right; and each piece where its part is.
 */
class RobotPartsTest {

    /** How far from its part's joint a piece's middle may lie, as many times as the model is tall, at most. */
    private static final double NEAR = 0.3;
    private static final double SAME = 0.01;

    private final Rig bound = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
    private final List<RobotParts.Piece> frame = RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT, Paint.FRAME,
            new Detail(0.8));

    @Test
    void everyPartHasItsPiecesTheTrunksAndHeadsItsOwnAndTheLimbsHoused() {
        for (final Part p : Part.values()) {
            if (p != Part.RIGHT_GIRDLE && p != Part.LEFT_GIRDLE) {
                assertTrue(frame.stream().anyMatch(piece -> piece.part() == p), p.said() + " has its pieces");
            }
        }
        assertTrue(frame.stream().anyMatch(p -> p.part() == Part.HEAD && p.paint() == Paint.VISOR), "a visor");
        for (final Part p : List.of(Part.RIGHT_UPPER_ARM, Part.LEFT_FOREARM, Part.RIGHT_THIGH, Part.LEFT_SHIN)) {
            assertTrue(frame.stream().anyMatch(piece -> piece.part() == p && piece.paint() == Paint.HOUSING),
                    p.said() + " housed");
        }
    }

    @Test
    void itsLeftIsItsRightInAMirrorAndEachPieceIsWhereItsPartIs() {
        for (final Part p : Part.values()) {
            final List<V3> mine = middles(p);
            for (final V3 m : mine) {
                final double far = m.minus(bound.frame(p).at()).length() / Proportions.MODEL.stature();
                assertTrue(far < NEAR, p.said() + ": a piece " + far + " of the body's height from its joint");
            }
            if (p.side() > 0) {
                final List<V3> other = middles(p.other());
                assertEquals(mine.size(), other.size(), p.said() + ": as many pieces each side");
                for (int i = 0; i < mine.size(); i++) {
                    final V3 mirrored = new V3(-mine.get(i).x(), mine.get(i).y(), mine.get(i).z());
                    assertTrue(mirrored.minus(other.get(i)).length() < SAME * Proportions.MODEL.stature(),
                            p.said() + ": its piece " + i + " mirrored");
                }
            }
        }
    }

    /** The middle of each of {@code p}'s pieces, the average of its points, in order. */
    private List<V3> middles(final Part p) {
        return frame.stream().filter(piece -> piece.part() == p).map(piece -> {
            final float[] at = piece.mesh().points();
            V3 sum = V3.ZERO;
            for (int i = 0; i < at.length; i += 3) {
                sum = sum.plus(new V3(at[i], at[i + 1], at[i + 2]));
            }
            return sum.times(3.0 / at.length);
        }).toList();
    }
}

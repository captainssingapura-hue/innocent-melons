package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cloth cut along a plane: what lies on the side kept, all of it, the cut's edge on the plane; mirrored across the
 * body's middle, the points on the middle shared, the other side following the other side's parts.
 */
class SheetTest {

    @Test
    void cutAlongAPlaneItKeepsAllOnOneSideItsEdgeOnThePlane() {
        final Sheet cut = square().cut(new V3(1.5, 0, 0), V3.X);
        double area = 0;
        for (int i = 0; i < cut.triangleCount(); i++) {
            final int[] t = cut.triangleAt(i);
            area += cut.point(t[1]).minus(cut.point(t[0])).cross(cut.point(t[2]).minus(cut.point(t[0]))).length() / 2;
        }
        assertEquals(2.5 * 4, area, 1e-9, "all of it beyond the cut");
        int onTheCut = 0;
        for (int i = 0; i < cut.size(); i++) {
            assertTrue(cut.point(i).x() >= 1.5 - 1e-9, "nothing before the cut: " + cut.point(i));
            onTheCut += Math.abs(cut.point(i).x() - 1.5) < 1e-9 ? 1 : 0;
        }
        // five across the squares' sides, four across their diagonals
        assertEquals(9, onTheCut, "its edge on the plane, each point once");
    }

    @Test
    void mirroredItsMiddleIsSharedItsOtherSideFollowsTheOtherSide() {
        final Sheet both = square().mirrored();
        assertEquals(25 + 20, both.size(), "the five points on the middle shared");
        for (int i = 25; i < both.size(); i++) {
            assertTrue(both.point(i).x() < 0);
            assertEquals(1, both.weightsAt(i).by()[Part.LEFT_THIGH.ordinal()], 1e-6);
        }
    }

    /** A sheet 4 cm square, from the middle out to the right, a point every centimetre, following the right thigh. */
    private Sheet square() {
        final Sheet s = new Sheet();
        final float[] alone = new float[Part.values().length];
        alone[Part.RIGHT_THIGH.ordinal()] = 1;
        final int[][] at = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                at[i][j] = s.add(new V3(i, j, 0), new Weights(alone));
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                s.quad(at[i][j], at[i + 1][j], at[i + 1][j + 1], at[i][j + 1]);
            }
        }
        return s;
    }
}

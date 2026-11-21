package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Bricks laid in each bond, of the common brick with a centimetre of mortar: where the joints fall, course over
 * course, none standing over another; which brick a point is in; and, from far off, the joints' share of the wall.
 */
class LayingTest {

    private final Seen seen = new Seen();

    @Test
    void lengthwiseEveryCourseIsHalfABrickAlongFromTheOneBelow() {
        final Laying laying = new Laying(new Brick(), Bond.STRETCHER, 1);
        assertEquals(0, mortar(laying, 10, 3), 1e-6, "on a brick of the first course");
        assertEquals(1, mortar(laying, 22, 3), 1e-6, "in the joint after it");
        assertEquals(1, mortar(laying, 10, 7), 1e-6, "in the bed the second course is laid in");
        assertEquals(0, mortar(laying, 22, 10), 1e-6, "over that joint, in the second course, a brick");
        assertEquals(1, mortar(laying, 10.75, 10), 1e-6, "whose own joint is half a brick back");
        assertEquals(1, mortar(laying, 22, 18), 1e-6, "and the third course's joint is over the first's again");
    }

    @Test
    void inEnglishBondACourseOfEndsLiesOverEachCourseLaidLengthwiseAnEndOverEachJoint() {
        final Laying laying = new Laying(new Brick(), Bond.ENGLISH, 1);
        assertEquals(1, mortar(laying, 22, 3), 1e-6, "the joint between two bricks laid lengthwise");
        assertEquals(0, mortar(laying, 22, 10), 1e-6, "an end's middle over it");
        assertEquals(1, mortar(laying, 16.4, 10), 1e-6, "the end's joint one side");
        assertEquals(1, mortar(laying, 27.6, 10), 1e-6, "and the other");
        assertEquals(0, mortar(laying, 33, 10), 1e-6, "and the next end");
    }

    @Test
    void inFlemishBondEndsAndLengthsTakeTurnsAndEachEndLiesOverTheMiddleOfALength() {
        final Laying laying = new Laying(new Brick(), Bond.FLEMISH, 1);
        assertEquals(0, mortar(laying, 10.75, 3), 1e-6, "a brick laid lengthwise");
        assertEquals(1, mortar(laying, 22, 3), 1e-6, "a joint");
        assertEquals(0, mortar(laying, 27.6, 3), 1e-6, "an end");
        assertEquals(1, mortar(laying, 33.25, 3), 1e-6, "a joint");
        assertEquals(0, mortar(laying, 10.75, 10), 1e-6, "in the next course, an end over the length's middle");
        assertEquals(1, mortar(laying, 5.1, 10), 1e-6, "its joint one side");
        assertEquals(1, mortar(laying, 16.4, 10), 1e-6, "and the other");
    }

    @Test
    void aPointIsInOneBrickOfOneCourseAndItsNeighbourInAnother() {
        final Laying laying = new Laying(new Brick(), Bond.FLEMISH, 1);
        assertEquals(0, course(laying, 5, 3));
        assertEquals(1, course(laying, 5, 10));
        assertEquals(-1, course(laying, 5, -2), "below the foot, counted backwards");
        assertEquals(brick(laying, 2, 3), brick(laying, 20, 1), "two points of one brick");
        assertEquals(0, brick(laying, 10, 3), "the first brick of the first course");
        assertEquals(1, brick(laying, 27, 3), "the end after it");
        assertEquals(2, brick(laying, 40, 3), "and the length after that");
        assertNotEquals(brick(laying, 10.75, 3), brick(laying, 10.75, 10), "the end over it is another course's");
    }

    @Test
    void fromFarOffTheJointsAreTheirShareOfTheWallEverywhere() {
        final Laying laying = new Laying(new Brick(), Bond.STRETCHER, 1);
        final double share = 1 - 21.5 * 6.5 / (22.5 * 7.5);
        for (final double[] at : new double[][] {{10, 3}, {22, 3}, {10, 7}, {-140, 200}}) {
            final Real mortar = laying.mortar(seen.at(), seen.covers());
            assertEquals(share, mortar.node().worked(seen.sample(new Vec3(at[0], at[1], 0), 300))[0], 0.005);
        }
    }

    private double mortar(final Laying laying, final double across, final double up) {
        return laying.mortar(seen.at(), seen.covers()).node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }

    private double course(final Laying laying, final double across, final double up) {
        return laying.course(seen.at()).node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }

    private double brick(final Laying laying, final double across, final double up) {
        return laying.brick(seen.at()).node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }
}

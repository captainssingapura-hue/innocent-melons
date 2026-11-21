package com.example.fxsuite.vulkan.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Matrices multiplied in the order they are done, and given to the card column by column. */
class Mat4Test {

    @Test
    void oneAfterAnotherIsTheFirstDoneAndThenTheSecond() {
        final Mat4 moved = new Mat4(new Vec3(10, 0, 0));
        final Mat4 turned = new Mat4(new Vec3(0, 0, 1), new Vec3(0, 1, 0), new Vec3(-1, 0, 0));
        final Clip movedThenTurned = turned.times(moved).clip(new Vec3(1, 2, 3));
        assertEquals(new Clip(3, 2, -11, 1), movedThenTurned, "moved 10 across, then said by the turned ways");
        final Clip turnedThenMoved = moved.times(turned).clip(new Vec3(1, 2, 3));
        assertEquals(new Clip(13, 2, -1, 1), turnedThenMoved);
    }

    @Test
    void theMatrixThatLeavesEveryPointWhereItIsDoes() {
        assertEquals(new Clip(4, -5, 6, 1), new Mat4().clip(new Vec3(4, -5, 6)));
    }

    @Test
    void itsNumbersGoToTheCardColumnByColumn() {
        final double[] columns = new Mat4(new Vec3(7, 8, 9)).columns();
        assertArrayEquals(new double[] {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 7, 8, 9, 1}, columns);
    }

    @Test
    void aMatrixOfAnyOtherCountOfNumbersIsRefusedAndAPointBehindTheCameraIsNotShown() {
        assertThrows(IllegalArgumentException.class, () -> new Mat4(new double[15]));
        assertThrows(IllegalArgumentException.class, () -> new Clip(1, 1, 1, -2).shown());
    }
}

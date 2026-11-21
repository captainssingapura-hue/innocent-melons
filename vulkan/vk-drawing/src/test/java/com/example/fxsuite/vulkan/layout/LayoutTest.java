package com.example.fxsuite.vulkan.layout;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A layout's fields set where the card will look for them, packed or as a block (by the card's own rule for blocks,
 * std140); and a block filled whole or not at all.
 */
class LayoutTest {

    private final Layout frame = new Layout("Frame", List.of(new Field("seen", Kind.MATRIX),
            new Field("sunTowards", Kind.TRIPLE), new Field("strength", Kind.REAL), new Field("sun", Kind.TRIPLE),
            new Field("ambient", Kind.TRIPLE)));

    @Test
    void packedEachFieldComesStraightAfterTheOneBefore() {
        assertEquals(0, frame.packedAt("seen"));
        assertEquals(64, frame.packedAt("sunTowards"));
        assertEquals(76, frame.packedAt("strength"));
        assertEquals(80, frame.packedAt("sun"));
        assertEquals(104, frame.packedBytes());
    }

    @Test
    void inABlockThreeNumbersStandWhereFourWouldAndALoneNumberFillsTheGapAfterThem() {
        assertEquals(0, frame.blockAt("seen"));
        assertEquals(64, frame.blockAt("sunTowards"));
        assertEquals(76, frame.blockAt("strength"), "in what three numbers leave of sixteen bytes");
        assertEquals(80, frame.blockAt("sun"));
        assertEquals(96, frame.blockAt("ambient"));
        assertEquals(112, frame.blockBytes(), "to the end of its last field, 108, rounded up to sixteens");
    }

    @Test
    void aBlockFilledHasEachNumberWhereItsFieldBegins() {
        final ByteBuffer filled = new Filling(new Layout("Light", List.of(new Field("sun", Kind.TRIPLE),
                new Field("ambient", Kind.TRIPLE)))).with("ambient", 0.5, 0.25, 0.125).with("sun", 1, 2, 3).filled();
        assertEquals(32, filled.remaining());
        assertEquals(2f, filled.getFloat(4));
        assertEquals(0.5f, filled.getFloat(16));
        assertEquals(0.125f, filled.getFloat(24));
    }

    @Test
    void aFieldTheLayoutHasNotAWrongCountOfNumbersAndAFieldNeverSetAreEachRefused() {
        final Layout light = new Layout("Light", List.of(new Field("sun", Kind.TRIPLE),
                new Field("ambient", Kind.TRIPLE)));
        assertThrows(IllegalArgumentException.class, () -> new Filling(light).with("moon", 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Filling(light).with("sun", 1, 1));
        assertThrows(IllegalStateException.class, () -> new Filling(light).with("sun", 1, 1, 1).filled());
    }

    @Test
    void twoFieldsOfOneNameAndALayoutOfNothingAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Layout("Light", List.of(new Field("sun", Kind.TRIPLE),
                new Field("sun", Kind.REAL))));
        assertThrows(IllegalArgumentException.class, () -> new Layout("Light", List.of()));
    }
}

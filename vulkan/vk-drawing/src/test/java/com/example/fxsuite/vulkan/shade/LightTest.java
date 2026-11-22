package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Where the sun stands, said as how high and which way round: the bench's day has its sun some sixty degrees up,
 * behind and to the left of whoever looks along the world; a sun set where it already stands shines the way it
 * did; a sun set anywhere is found there again, as bright and of the same colour; straight overhead it shines
 * straight down; and low ahead it shines level, back along the world.
 */
class LightTest {

    /** The bench's day: its sun from above and a little to one side, over a sky and a ground to be mirrored. */
    private final Light day = new Light(new Vec3(0.45, -1, 0.35), new Vec3(0.6, 0.59, 0.56),
            new Vec3(0.56, 0.58, 0.61), new Vec3(0.79, 0.86, 0.91), new Vec3(0.36, 0.37, 0.35));

    @Test
    void theBenchsSunIsSomeSixtyDegreesUpBehindAndToTheLeftAndSetWhereItStandsShinesAsItDid() {
        assertEquals(60.3, day.sunHigh(), 0.1);
        assertEquals(232.1, day.sunRound(), 0.1);
        final Vec3 was = day.sunTowards().unit();
        final Vec3 set = day.sunAt(day.sunHigh(), day.sunRound()).sunTowards();
        assertEquals(was.x(), set.x(), 1e-12);
        assertEquals(was.y(), set.y(), 1e-12);
        assertEquals(was.z(), set.z(), 1e-12);
    }

    @Test
    void aSunSetAnywhereIsFoundThereAgainAsBrightAndOfTheSameColour() {
        for (final double high : new double[] {5, 30, 60, 89}) {
            for (final double round : new double[] {0, 45, 180, 270, 359}) {
                final Light set = day.sunAt(high, round);
                assertEquals(high, set.sunHigh(), 1e-9);
                assertEquals(round, set.sunRound(), 1e-9);
                assertEquals(1, set.sunTowards().length(), 1e-12);
                assertEquals(day.sun(), set.sun());
                assertEquals(day.ambient(), set.ambient());
                assertEquals(day.sky(), set.sky());
                assertEquals(day.ground(), set.ground());
            }
        }
    }

    @Test
    void straightOverheadItShinesStraightDownAndLowAheadItShinesLevelBackAlongTheWorld() {
        final Vec3 down = day.sunAt(90, 123).sunTowards();
        assertEquals(0, down.x(), 1e-12);
        assertEquals(-1, down.y(), 1e-12);
        assertEquals(0, down.z(), 1e-12);
        final Vec3 level = day.sunAt(0, 0).sunTowards();
        assertEquals(0, level.x(), 1e-12);
        assertEquals(0, level.y(), 1e-12);
        assertEquals(-1, level.z(), 1e-12);
    }
}

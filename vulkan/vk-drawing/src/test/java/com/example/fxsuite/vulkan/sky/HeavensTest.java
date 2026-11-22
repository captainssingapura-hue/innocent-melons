package com.example.fxsuite.vulkan.sky;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The sun and the moon worked out from a place and a moment, held against what was seen: the sun over the equator
 * at an equinox and as far north and south as it goes at the solstices; over London on midsummer's day, on the
 * horizon at the hours it rose and set and due south at noon as high as London's place allows; all but overhead at
 * Singapore's noon at the equinox, having risen due east; up at midnight in Tromso's summer and down at noon in its
 * winter; the moon's face all lit at a full moon, none of it at a new, and half at a quarter; the moon over the sun
 * at Dallas in the middle of the eclipse of April 2024; and a full moon high in London's midwinter midnight.
 */
class HeavensTest {

    private final Site london = new Site(51.5074, -0.1278);
    private final Site singapore = new Site(1.29, 103.85);
    private final Site tromso = new Site(69.65, 18.96);
    private final Site dallas = new Site(32.7767, -96.7970);

    @Test
    void theSunIsOverTheEquatorAtAnEquinoxAndAsFarNorthAndSouthAsItGoesAtTheSolstices() {
        assertEquals(0, new Sun().at(Instant.parse("2024-03-20T03:06:00Z")).north(), 0.02);
        assertEquals(23.44, new Sun().at(Instant.parse("2024-06-20T20:51:00Z")).north(), 0.02);
        assertEquals(-23.44, new Sun().at(Instant.parse("2024-12-21T09:21:00Z")).north(), 0.02);
    }

    @Test
    void overLondonOnMidsummersDayItIsOnTheHorizonWhenItRoseAndSetAndDueSouthAtNoonAsHighAsLondonAllows() {
        final Heavens over = new Heavens(london);
        // it is seen to rise and set with its middle most of a degree below level, the air lifting it into view
        final Stands rose = over.at(Instant.parse("2024-06-20T03:43:00Z")).sun();
        final Stands set = over.at(Instant.parse("2024-06-20T20:21:00Z")).sun();
        assertEquals(-0.85, rose.high(), 0.4);
        assertEquals(-0.85, set.high(), 0.4);
        assertTrue(rose.round() > 45 && rose.round() < 55, "in the north-east: " + rose);
        assertTrue(set.round() > 305 && set.round() < 315, "in the north-west: " + set);
        final Stands noon = over.at(Instant.parse("2024-06-20T12:02:00Z")).sun();
        assertEquals(90 - 51.5074 + 23.44, noon.high(), 0.05);
        assertEquals(180, noon.round(), 1);
    }

    @Test
    void atSingaporesNoonAtTheEquinoxItIsAllButOverheadHavingRisenDueEast() {
        final Heavens over = new Heavens(singapore);
        assertTrue(over.at(Instant.parse("2024-03-20T05:12:00Z")).sun().high() > 88.3);
        final Stands rising = over.at(Instant.parse("2024-03-19T23:10:00Z")).sun();
        assertEquals(0, rising.high(), 1.5);
        assertEquals(90, rising.round(), 1);
    }

    @Test
    void itIsUpAtMidnightInTromsosSummerAndDownAtNoonInItsWinter() {
        final Heavens over = new Heavens(tromso);
        final Stands midnight = over.at(Instant.parse("2024-06-20T22:44:00Z")).sun();
        assertEquals(69.65 + 23.44 - 90, midnight.high(), 0.1);
        assertEquals(0, Math.min(midnight.round(), 360 - midnight.round()), 1, "due north");
        assertTrue(!over.at(Instant.parse("2024-12-21T10:44:00Z")).sun().up());
    }

    @Test
    void theMoonsFaceIsAllLitAtAFullMoonNoneOfItAtANewAndHalfAtAQuarter() {
        final Heavens over = new Heavens(london);
        assertTrue(over.at(Instant.parse("2024-01-25T17:54:00Z")).moonLit() > 0.995, "full");
        assertTrue(over.at(Instant.parse("2000-01-21T04:44:00Z")).moonLit() > 0.999, "full, and eclipsed");
        assertTrue(over.at(Instant.parse("2024-01-11T11:57:00Z")).moonLit() < 0.005, "new");
        assertEquals(0.5, over.at(Instant.parse("2024-01-18T03:53:00Z")).moonLit(), 0.03, "first quarter");
    }

    @Test
    void theMoonIsOverTheSunAtDallasInTheMiddleOfTheEclipseOfApril2024() {
        final Sky sky = new Heavens(dallas).at(Instant.parse("2024-04-08T18:42:00Z"));
        assertTrue(sky.sun().high() > 60 && sky.sun().high() < 68, "the sun high in the early afternoon");
        assertTrue(apart(sky.sun(), sky.moon()) < 0.5, "the moon before the sun: " + apart(sky.sun(), sky.moon())
                + " degrees apart, each half a degree wide");
        assertTrue(sky.moonLit() < 0.001, "and none of its face lit");
    }

    @Test
    void aFullMoonStandsHighInLondonsMidwinterMidnightOppositeTheSun() {
        final Sky sky = new Heavens(london).at(Instant.parse("2024-01-26T00:10:00Z"));
        assertTrue(sky.moon().high() > 50, "high: " + sky.moon());
        assertTrue(sky.sun().high() < -50, "the sun as far below");
        assertEquals(180, Math.abs(sky.moon().round() - sky.sun().round()), 12, "and the other way round");
    }

    @Test
    void noPlaceIsFurtherNorthThanAPoleOrFurtherEastThanHalfRound() {
        assertThrows(IllegalArgumentException.class, () -> new Site(90.1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Site(0, -180.5));
    }

    /** How far apart in the sky two things stand, in degrees. */
    private double apart(final Stands one, final Stands other) {
        final double mine = Math.toRadians(one.high());
        final double theirs = Math.toRadians(other.high());
        return Math.toDegrees(Math.acos(Math.clamp(Math.sin(mine) * Math.sin(theirs) + Math.cos(mine)
                * Math.cos(theirs) * Math.cos(Math.toRadians(one.round() - other.round())), -1, 1)));
    }
}

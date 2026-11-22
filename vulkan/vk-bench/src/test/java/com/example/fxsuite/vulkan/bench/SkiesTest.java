package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.worked.Daylight;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The light and the sky of a walk's frames. By hand: the bench's day with its sun where the sliders put it, or its
 * night. By the almanac and the clock: the clock begins at the moment asked and goes on so many times as fast as
 * the simulation's time, stands still when it is asked to, and begins again when it is asked to begin again; over
 * Singapore it is day at noon and night at midnight, the sun and the moon standing where the almanac has them; an
 * almanac is told in words, and what cannot be read is refused; and each pace the slider offers is faster than
 * the last.
 */
class SkiesTest {

    private final ZoneId singapore = ZoneId.of("Asia/Singapore");
    private final Instant noon = Instant.parse("2024-03-20T05:12:00Z");

    @Test
    void byHandItIsTheBenchsDayWithItsSunWhereTheSlidersPutItOrItsNight() {
        final Asked asked = new Asked(1, true, new Asked.Sun(40, 100));
        final Skies skies = new Skies(asked);
        final Daylight day = skies.lit();
        assertEquals(new Daylight().sky(), day.sky());
        assertEquals(40, day.light().sunHigh(), 1e-9);
        assertEquals(100, day.light().sunRound(), 1e-9);
        asked.night(true);
        final Daylight night = skies.lit();
        assertEquals(new Daylight().night().sky(), night.sky());
        assertEquals(40, night.light().sunHigh(), 1e-9, "the moon where the sun stood");
    }

    @Test
    void theClockBeginsAtTheMomentAskedAndRunsSoManyTimesAsFastAsTheSimulationsTime() {
        final Asked asked = new Asked(1);
        asked.almanac(new Asked.Almanac(1.283, 103.86, noon, singapore));
        asked.quickened(3_600);
        final Skies skies = new Skies(asked);
        assertEquals(noon, skies.now());
        for (int step = 0; step < 120; step++) {
            skies.stepped(1.0 / 120);
        }
        assertEquals(Duration.ofHours(1).toMillis(), Duration.between(noon, skies.now()).toMillis(), 1,
                "an hour gone in a second");
        asked.quickened(0);
        final Instant stood = skies.now();
        skies.stepped(5);
        assertEquals(stood, skies.now(), "standing still");
        asked.almanac(new Asked.Almanac(1.283, 103.86, noon, singapore));
        skies.stepped(0);
        assertEquals(noon, skies.now(), "begun again");
    }

    @Test
    void overSingaporeItIsDayAtNoonAndNightAtMidnightTheSunAndMoonWhereTheAlmanacHasThem() {
        final Asked asked = new Asked(1);
        asked.clocked(true);
        asked.quickened(0);
        asked.almanac(new Asked.Almanac(1.283, 103.86, noon, singapore));
        final Skies skies = new Skies(asked);
        assertTrue(skies.sky().sun().high() > 88, "the sun all but overhead at the equinox's noon");
        assertEquals(new Daylight().sky(), skies.lit().sky());
        assertEquals(skies.sky().sun().high(), skies.lit().light().sunHigh(), 1e-6, "the light from where it stands");
        assertTrue(skies.said().startsWith("Wed 20 Mar 2024 13:12 (Asia/Singapore)\nSun 8"), skies.said());
        asked.almanac(new Asked.Almanac(1.283, 103.86, noon.plus(Duration.ofHours(12)), singapore));
        skies.stepped(0);
        assertTrue(skies.sky().sun().high() < -80, "and all but underfoot at midnight");
        final Daylight night = skies.lit();
        assertTrue(night.sky().minus(new Daylight().night().sky()).length() < 1e-9, "the night's sky");
        // the moon was past its first quarter and up that night: it is what lights it
        assertTrue(skies.sky().moon().up() && skies.sky().moonLit() > 0.7, skies.said());
        assertEquals(skies.sky().moon().high(), night.light().sunHigh(), 1e-6, "the light from where the moon stands");
    }

    @Test
    void anAlmanacIsToldInWordsABlankLeavingAThingAsItIsAndWhatCannotBeReadIsRefused() {
        final Asked.Almanac first = new Asked.Almanac(1.283, 103.86, noon, singapore);
        assertEquals(first, first.told("", "", " "));
        final Asked.Almanac london = first.told("51.5074, -0.1278", "2024-06-20T13:02", "Europe/London");
        assertEquals(51.5074, london.latitude());
        assertEquals(-0.1278, london.longitude());
        assertEquals(Instant.parse("2024-06-20T12:02:00Z"), london.begins(), "an hour ahead of Greenwich in summer");
        assertEquals(ZoneId.of("Europe/London"), london.zone());
        assertEquals(Instant.parse("2024-06-20T05:02:00Z"), first.told("", "2024-06-20T13:02", "").begins(),
                "by the clocks it already had");
        assertThrows(RuntimeException.class, () -> first.told("51.5", "", ""));
        assertThrows(RuntimeException.class, () -> first.told("95, 10", "", ""));
        assertThrows(RuntimeException.class, () -> first.told("", "the day after tomorrow", ""));
        assertThrows(RuntimeException.class, () -> first.told("", "", "Mars/Olympus"));
    }

    @Test
    void eachPaceIsFasterThanTheLastFromStandingStillToADayASecond() {
        double last = -1;
        for (final Pace pace : Pace.values()) {
            assertTrue(pace.times() > last, pace + " faster than the one before");
            assertEquals(pace, Pace.STILL.nearest(pace.times()));
            last = pace.times();
        }
        assertEquals(0, Pace.STILL.times());
        assertEquals(86_400, Pace.DAY.times());
        assertEquals(Pace.DAY, Pace.STILL.nearest(1e9), "nothing faster than the fastest");
    }
}

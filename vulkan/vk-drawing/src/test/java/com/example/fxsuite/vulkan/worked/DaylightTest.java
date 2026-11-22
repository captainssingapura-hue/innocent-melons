package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.sky.Sky;
import com.example.fxsuite.vulkan.sky.Stands;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bench's day, and the same place by night: by night the sky is dark and blue; the moon shines the way the sun
 * did, much dimmer than the sun and bluer; the light on everything is all but gone; and what is mirrored from above
 * is that dark sky, and from below a ground darker still. And the same place at any hour, by where the sun and the
 * moon stand: the day under a high sun, the night under a full moon with the sun well down; half the moon's light
 * from a moon half lit, and no light that casts a shadow with neither up; nothing jumping as the sun crosses the
 * horizon, where the sky is warmed; and a low sun dimmer and redder, shining from no lower than three degrees.
 */
class DaylightTest {

    private final Daylight day = new Daylight();
    private final Daylight night = day.night();

    @Test
    void byNightTheSkyIsDarkAndBlueAndIsWhatIsMirroredFromAbove() {
        assertTrue(bright(night.sky()) < bright(day.sky()) / 8, "dark: " + night.sky());
        assertTrue(night.sky().z() > 2 * night.sky().x(), "and blue");
        assertEquals(night.sky(), night.light().sky());
        assertTrue(bright(night.light().ground()) < bright(night.sky()), "the ground darker still");
    }

    @Test
    void theMoonShinesTheWayTheSunDidMuchDimmerAndBluerAndLittleLightFallsOnEverything() {
        final Light sun = day.light();
        final Light moon = night.light();
        assertEquals(sun.sunTowards(), moon.sunTowards());
        assertTrue(bright(moon.sun()) < bright(sun.sun()) / 2.5, "dimmer: " + moon.sun());
        assertTrue(moon.sun().z() / moon.sun().x() > 1.5 * sun.sun().z() / sun.sun().x(), "and bluer");
        assertTrue(bright(moon.ambient()) < bright(sun.ambient()) / 8, "little on everything: " + moon.ambient());
        assertTrue(bright(moon.ambient()) < bright(moon.sun()) / 2, "so the moon's shadows are deep");
    }

    @Test
    void underAHighSunItIsTheDayAndWithTheSunWellDownAndAFullMoonHighItIsTheNight() {
        final Daylight noon = day.under(new Sky(new Stands(60, 180), new Stands(-40, 0), 0.5), night);
        near(day.sky(), noon.sky());
        near(day.light().sun(), noon.light().sun());
        near(day.light().ambient(), noon.light().ambient());
        assertEquals(60, noon.light().sunHigh(), 1e-9, "shining from where the sun stands");
        assertEquals(180, noon.light().sunRound(), 1e-9);
        final Daylight midnight = day.under(new Sky(new Stands(-40, 0), new Stands(50, 170), 1), night);
        near(night.sky(), midnight.sky());
        near(night.light().sun(), midnight.light().sun());
        near(night.light().ambient(), midnight.light().ambient());
        assertEquals(50, midnight.light().sunHigh(), 1e-9, "shining from where the moon stands");
        assertEquals(170, midnight.light().sunRound(), 1e-9);
    }

    @Test
    void aMoonHalfLitGivesHalfTheLightAndWithNeitherUpNothingCastsAShadow() {
        final Daylight half = day.under(new Sky(new Stands(-40, 0), new Stands(50, 170), 0.5), night);
        assertEquals(bright(night.light().sun()) / 2, bright(half.light().sun()), 1e-9);
        final Daylight neither = day.under(new Sky(new Stands(-40, 0), new Stands(-20, 170), 1), night);
        assertEquals(0, bright(neither.light().sun()), 1e-12, "no light that casts a shadow");
        near(night.light().ambient(), neither.light().ambient());
        // a moon up by day is not what lights the day
        final Daylight both = day.under(new Sky(new Stands(60, 180), new Stands(50, 90), 1), night);
        assertEquals(180, both.light().sunRound(), 1e-9);
    }

    @Test
    void nothingJumpsAsTheSunCrossesTheHorizonWhereTheSkyIsWarmed() {
        final Stands moon = new Stands(50, 170);
        final Daylight under = day.under(new Sky(new Stands(-0.05, 90), moon, 1), night);
        final Daylight over = day.under(new Sky(new Stands(0.05, 90), moon, 1), night);
        assertTrue(bright(under.light().sun()) < 0.01 && bright(over.light().sun()) < 0.01,
                "the moon's light all but gone as the sun's begins");
        assertTrue(under.sky().minus(over.sky()).length() < 0.02, "the sky all but the same either side");
        assertTrue(under.light().ambient().minus(over.light().ambient()).length() < 0.02);
        final Daylight rising = day.under(new Sky(new Stands(0, 90), moon, 1), night);
        assertTrue(rising.sky().x() > rising.sky().z(), "warm at sunrise: " + rising.sky());
        assertTrue(day.sky().x() < day.sky().z() && night.sky().x() < night.sky().z(), "as neither day nor night is");
    }

    @Test
    void aLowSunIsDimmerAndRedderAndShinesFromNoLowerThanThreeDegrees() {
        final Daylight low = day.under(new Sky(new Stands(6, 270), new Stands(-40, 0), 1), night);
        assertTrue(bright(low.light().sun()) < 0.5 * bright(day.light().sun()), "dimmer");
        assertTrue(low.light().sun().z() / low.light().sun().x()
                < 0.8 * day.light().sun().z() / day.light().sun().x(), "and redder");
        assertEquals(6, low.light().sunHigh(), 1e-9);
        assertEquals(3, day.under(new Sky(new Stands(1, 270), new Stands(-40, 0), 1), night).light().sunHigh(),
                1e-9, "from no lower than three degrees");
    }

    private void near(final Vec3 wanted, final Vec3 was) {
        assertTrue(wanted.minus(was).length() < 1e-9, wanted + " and " + was);
    }

    private double bright(final Vec3 light) {
        return light.x() + light.y() + light.z();
    }
}

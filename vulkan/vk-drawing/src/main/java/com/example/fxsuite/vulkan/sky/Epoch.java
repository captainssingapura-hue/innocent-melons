package com.example.fxsuite.vulkan.sky;

import java.time.Instant;

/**
 * A moment, counted as the sky's arithmetic counts it: in days, and in centuries of 36525 days, from noon at
 * Greenwich on 1 January 2000. The few seconds by which the clocks' time has since slipped from the Earth's own
 * turning are left out: they move the moon by a hundredth of a degree.
 */
record Epoch(Instant when) {

    /** Noon at Greenwich on 1 January 2000, in seconds since the start of 1970. */
    private static final long NOON_2000 = 946_728_000L;
    private static final double DAY = 86_400;
    private static final double CENTURY = 36_525;
    private static final double WHOLE = 360;

    double days() {
        return (when.getEpochSecond() - NOON_2000 + when.getNano() / 1e9) / DAY;
    }

    double centuries() {
        return days() / CENTURY;
    }

    /**
     * How far the stars have turned over a place {@code longitude} degrees east of Greenwich: the place's own
     * star time, as an angle in degrees from 0 up to 360.
     */
    double turned(final double longitude) {
        return ((280.46061837 + 360.98564736629 * days() + longitude) % WHOLE + WHOLE) % WHOLE;
    }
}

package com.example.fxsuite.vulkan.bench;

/**
 * How fast the sky's clock runs, as a slider offers it: so many times as fast as time does, and what to call
 * that. From standing still, through time's own pace, up to a whole day in a second.
 */
public enum Pace {
    STILL(0, "standing still"),
    TIME(1, "as fast as time runs"),
    TEN(10, "ten times as fast as time"),
    MINUTE(60, "a minute a second"),
    TEN_MINUTES(600, "ten minutes a second"),
    HOUR(3_600, "an hour a second"),
    SIX_HOURS(21_600, "six hours a second"),
    DAY(86_400, "a day a second");

    private final double times;
    private final String said;

    Pace(final double times, final String said) {
        this.times = times;
        this.said = said;
    }

    /** How many times as fast as time. */
    public double times() {
        return times;
    }

    /** What to call it. */
    public String said() {
        return said;
    }

    /** The pace nearest to running {@code quickened} times as fast as time: the slowest that is as fast. */
    public Pace nearest(final double quickened) {
        for (final Pace pace : values()) {
            if (pace.times >= quickened) {
                return pace;
            }
        }
        return DAY;
    }
}

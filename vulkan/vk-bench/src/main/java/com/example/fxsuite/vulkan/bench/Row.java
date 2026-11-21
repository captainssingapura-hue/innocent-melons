package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A row of the same subject at different distances, to see in one picture how it comes down as it goes further off.
 * Along the row, side by side and all the same size, each is shown as from twice as far as the one before it: no
 * smaller, but looking as it would from there. Over each stands the same subject really that many times as far from
 * the eye, as small as it then is: what the game would show, and what the one below it is the enlargement of.
 *
 * <p>{@code middle} is where the row's middle stands, {@code count} how many there are in it, {@code apart} how far
 * each is from the next, and {@code lift} how far above each the far one is seen, all in centimetres.
 */
public record Row(Vec3 middle, int count, double apart, double lift) {

    private static final double TWICE = 2;

    /** Where the one numbered {@code which} stands in the row, the first at one end. */
    public Vec3 near(final int which) {
        return middle.plus(new Vec3((which - (count - 1) / 2.0) * apart, 0, 0));
    }

    /** How many times as far off the one numbered {@code which} is shown as from, the first as from {@code first}. */
    public double times(final int which, final double first) {
        return first * Math.pow(TWICE, which);
    }

    /**
     * Where the far one over the one numbered {@code which} stands, for an eye at {@code eye}: {@code times} as far
     * from the eye as the place just above the near one, and straight beyond that place, so that is where it is
     * seen.
     */
    public Vec3 far(final int which, final double times, final Vec3 eye) {
        return eye.plus(near(which).plus(new Vec3(0, lift, 0)).minus(eye).times(times));
    }
}

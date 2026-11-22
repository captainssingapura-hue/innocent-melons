package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The upper half of a round body, as a melon cut in two along its length and set down on its cut: {@code across}
 * long, {@code along} wide, and half of {@code up} high, standing on its frame's ground with its middle over the
 * frame's middle and its two ends on the ground. Its surface faces as the whole body's would, so it is lit as a
 * round thing. It has no underside: what it stands on hides that.
 */
public record Dome(double across, double up, double along) {

    public Dome {
        if (!(across > 0 && up > 0 && along > 0)) {
            throw new IllegalArgumentException("a body has some size each way: " + across + " by " + up + " by "
                    + along);
        }
    }

    /**
     * Its surface as triangles: {@code rings} bands from one end to the other, each band {@code round} steps over the
     * top from one side's foot to the other's.
     */
    public Mesh formed(final int round, final int rings) {
        if (round < 2 || rings < 2) {
            throw new IllegalArgumentException("too few points to go over a dome: " + round + " over, " + rings
                    + " bands");
        }
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int first = point(forming, 0, 0);
        for (int ring = 1; ring < rings; ring++) {
            for (int step = 0; step <= round; step++) {
                point(forming, Math.PI * ring / rings, Math.PI * step / round - Math.PI / 2);
            }
        }
        final int last = point(forming, Math.PI, 0);
        final int each = round + 1;
        for (int step = 0; step < round; step++) {
            forming.triangle(first, first + 1 + step, first + 2 + step);
            forming.triangle(last, last - each + step, last - each + step + 1);
            for (int ring = 1; ring < rings - 1; ring++) {
                final int here = first + 1 + each * (ring - 1) + step;
                final int there = here + each;
                forming.triangle(here, here + 1, there);
                forming.triangle(here + 1, there + 1, there);
            }
        }
        return forming.formed();
    }

    /**
     * The point {@code from} round from its first end toward its other, {@code turn} over from straight up toward
     * one side, added to {@code forming} with the way the surface faces there: its number.
     */
    private int point(final Forming forming, final double from, final double turn) {
        final Vec3 at = new Vec3(across / 2 * Math.cos(from), up / 2 * Math.sin(from) * Math.cos(turn),
                along / 2 * Math.sin(from) * Math.sin(turn));
        // square to the surface, which on a body not a ball is not the way from its middle
        final Vec3 faces = new Vec3(at.x() / (across * across), at.y() / (up * up), at.z() / (along * along));
        return forming.point(at, faces);
    }
}

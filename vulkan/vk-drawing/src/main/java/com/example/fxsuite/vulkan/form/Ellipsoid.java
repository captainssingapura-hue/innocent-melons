package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A round body, longer one way than another, as a fruit is: how long it is across, how tall, and how far along, in
 * centimetres, its middle at its frame's middle. Its ends are on its line across.
 */
public record Ellipsoid(double across, double up, double along) {

    public Ellipsoid {
        if (!(across > 0 && up > 0 && along > 0)) {
            throw new IllegalArgumentException("a body has some size every way: " + across + ", " + up + ", " + along);
        }
    }

    /**
     * Its surface as triangles: {@code round} points round it at each of its rings, {@code rings} bands from end to
     * end, a single point at each end. At each point the surface faces as the body's own does there, not as the
     * triangles do, so it is lit as a round thing.
     */
    public Mesh formed(final int round, final int rings) {
        if (round < 3 || rings < 2) {
            throw new IllegalArgumentException("too few points to go round a body: " + round + " round, " + rings
                    + " bands");
        }
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int first = forming.point(at(0, 0), facing(0, 0));
        for (int ring = 1; ring < rings; ring++) {
            for (int step = 0; step < round; step++) {
                forming.point(at(Math.PI * ring / rings, turn(step, round)),
                        facing(Math.PI * ring / rings, turn(step, round)));
            }
        }
        final int last = forming.point(at(Math.PI, 0), facing(Math.PI, 0));
        for (int step = 0; step < round; step++) {
            final int next = (step + 1) % round;
            forming.triangle(first, first + 1 + step, first + 1 + next);
            forming.triangle(last, last - round + step, last - round + next);
            for (int ring = 1; ring < rings - 1; ring++) {
                final int here = first + 1 + round * (ring - 1);
                final int there = here + round;
                forming.triangle(here + step, here + next, there + step);
                forming.triangle(here + next, there + next, there + step);
            }
        }
        return forming.formed();
    }

    private double turn(final int step, final int round) {
        return 2 * Math.PI * step / round;
    }

    /** The point {@code from} round from its first end toward its other, {@code turn} round its line across. */
    private Vec3 at(final double from, final double turn) {
        return new Vec3(across / 2 * Math.cos(from), up / 2 * Math.sin(from) * Math.cos(turn),
                along / 2 * Math.sin(from) * Math.sin(turn));
    }

    /** The way its surface faces there: square to it, which on a body not a ball is not the way from its middle. */
    private Vec3 facing(final double from, final double turn) {
        return new Vec3(Math.cos(from) / (across / 2), Math.sin(from) * Math.cos(turn) / (up / 2),
                Math.sin(from) * Math.sin(turn) / (along / 2));
    }
}

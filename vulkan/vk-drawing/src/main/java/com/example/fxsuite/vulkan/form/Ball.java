package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A ball at a place: {@code out} from its middle to its surface, with {@code round} points round it and half as many
 * bands from its top to its foot. What a point of a thing drawn as a graph becomes when its lines are given a
 * thickness: where two bars meet at an angle, a ball as thick as they are closes the gap their square ends leave.
 */
public record Ball(Vec3 middle, double out, int round) {

    public Ball {
        if (!(out > 0) || round < 4) {
            throw new IllegalArgumentException("a ball has some size, and four points round it at least: " + out
                    + " out, " + round + " round");
        }
    }

    /** Its surface added to {@code forming}, each face turned the way it faces. */
    public void formed(final Forming forming) {
        final int bands = round / 2;
        final int top = forming.point(middle.plus(new Vec3(0, out, 0)), new Vec3(0, 1, 0));
        for (int band = 1; band < bands; band++) {
            for (int step = 0; step < round; step++) {
                final Vec3 faces = faces(Math.PI * band / bands, 2 * Math.PI * step / round);
                forming.point(middle.plus(faces.times(out)), faces);
            }
        }
        final int foot = forming.point(middle.plus(new Vec3(0, -out, 0)), new Vec3(0, -1, 0));
        for (int step = 0; step < round; step++) {
            final int next = (step + 1) % round;
            forming.faced(top, top + 1 + step, top + 1 + next);
            forming.faced(foot, foot - round + step, foot - round + next);
            for (int band = 1; band < bands - 1; band++) {
                final int here = top + 1 + round * (band - 1);
                final int there = here + round;
                forming.faced(here + step, here + next, there + step);
                forming.faced(here + next, there + next, there + step);
            }
        }
    }

    /** The way from its middle to the point {@code down} from its top and {@code turn} round it. */
    private Vec3 faces(final double down, final double turn) {
        return new Vec3(Math.sin(down) * Math.cos(turn), Math.cos(down), Math.sin(down) * Math.sin(turn));
    }
}

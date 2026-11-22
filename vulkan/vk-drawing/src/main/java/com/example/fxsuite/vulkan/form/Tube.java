package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A straight round bar from one place to another: {@code thick} from its middle line to its surface, with {@code
 * round} flat sides that are lit as a round thing, and its two ends closed. What a line of a thing drawn as a graph
 * becomes when it is given a thickness: a frame's tube, a wishbone's arm, a rim's hoop piece by piece.
 */
public record Tube(Vec3 from, Vec3 to, double thick, int round) {

    public Tube {
        if (!(thick > 0) || round < 3 || !(to.minus(from).length() > 0)) {
            throw new IllegalArgumentException("a bar has some length and some thickness, and three sides at least: "
                    + from + " to " + to + ", " + thick + " thick, " + round + " sides");
        }
    }

    /** Its sides and its two ends added to {@code forming}, each face turned the way it faces. */
    public void formed(final Forming forming) {
        final Vec3 along = to.minus(from).unit();
        // two ways square to it and to each other, to go round it by
        final Vec3 one = (Math.abs(along.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0)).cross(along).unit();
        final Vec3 other = along.cross(one);
        final int first = forming.point(from.plus(out(one, other, 0).times(thick)), out(one, other, 0));
        forming.point(to.plus(out(one, other, 0).times(thick)), out(one, other, 0));
        for (int step = 1; step < round; step++) {
            final Vec3 out = out(one, other, step);
            forming.point(from.plus(out.times(thick)), out);
            forming.point(to.plus(out.times(thick)), out);
        }
        for (int step = 0; step < round; step++) {
            final int here = first + 2 * step;
            final int next = first + 2 * ((step + 1) % round);
            forming.faced(here, next, here + 1);
            forming.faced(next, next + 1, here + 1);
        }
        end(forming, from, along.times(-1), one, other);
        end(forming, to, along, one, other);
    }

    /** One closed end at {@code middle}, facing {@code faces}. */
    private void end(final Forming forming, final Vec3 middle, final Vec3 faces, final Vec3 one, final Vec3 other) {
        final int centre = forming.point(middle, faces);
        for (int step = 0; step < round; step++) {
            forming.point(middle.plus(out(one, other, step).times(thick)), faces);
        }
        for (int step = 0; step < round; step++) {
            forming.faced(centre, centre + 1 + step, centre + 1 + (step + 1) % round);
        }
    }

    /** The way out from its middle line at step {@code step} of the way round it. */
    private Vec3 out(final Vec3 one, final Vec3 other, final int step) {
        final double turn = 2 * Math.PI * step / round;
        return one.times(Math.cos(turn)).plus(other.times(Math.sin(turn)));
    }
}

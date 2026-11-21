package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Turns;

/**
 * A course as terms: along it, where its upright joints fall and which of its bricks a point is in. {@code along} is
 * how far along the course a point is, in centimetres, from where the course's faces begin to come round.
 */
record Run(Course course, double joint) {

    /**
     * How much of what a pixel covers is upright joint: each face's joint a stripe of its own, coming round with the
     * course, as fine as it is looked at and fading to its share of the course far off.
     */
    Real joints(final Real along, final Real covers) {
        final double repeat = course.repeat(joint);
        Real joints = new Real(0);
        for (int face = 0; face < course.faces().size(); face++) {
            final double ends = course.begins(face, joint) + course.faces().get(face);
            joints = joints.plus(new Turns(along.less(new Real(ends)).over(repeat), new Real(1 / repeat))
                    .pulse(joint / repeat, covers));
        }
        return joints;
    }

    /** Which brick of the course the point is in, counted along it: a whole number, below nothing before its start. */
    Real brick(final Real along) {
        final double repeat = course.repeat(joint);
        final Real turn = along.over(repeat);
        Real brick = turn.floored().times(course.faces().size());
        for (int face = 1; face < course.faces().size(); face++) {
            brick = brick.plus(turn.part().past(new Real(course.begins(face, joint) / repeat)));
        }
        return brick;
    }
}

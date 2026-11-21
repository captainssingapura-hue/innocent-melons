package com.example.fxsuite.texture.brick;

import java.util.List;

/**
 * How a wall's bricks are laid so that no joint stands over another: which faces each course shows, and how far each
 * course is shifted along from the one below. Courses are counted from the wall's foot, the first even.
 */
public enum Bond {

    /** Every brick lengthwise, each course half a brick along from the one below (十字缝). */
    STRETCHER,
    /** A course of bricks lengthwise, then a course of ends, each end over the middle of a joint or of a brick. */
    ENGLISH,
    /** Lengthwise and end by turns along every course (梅花丁), each end over the middle of the brick below. */
    FLEMISH,
    /**
     * Three lengthwise and then an end, along every course (三顺一丁), each end over the middle one of the three
     * below: the usual bond of old Chinese brick walls.
     */
    THREE_AND_ONE;

    /** The course the even courses are laid as: unshifted. */
    public Course even(final Brick brick, final double joint) {
        return switch (this) {
            case STRETCHER, ENGLISH -> new Course(List.of(brick.length()), 0);
            case FLEMISH -> new Course(List.of(brick.length(), brick.width()), 0);
            case THREE_AND_ONE -> new Course(List.of(brick.length(), brick.length(), brick.length(), brick.width()),
                    0);
        };
    }

    /** The course the odd courses are laid as, shifted along from the even. */
    public Course odd(final Brick brick, final double joint) {
        final double lengthwise = brick.length() + joint;
        return switch (this) {
            case STRETCHER -> new Course(List.of(brick.length()), lengthwise / 2);
            // an end's middle over the middle of the joint after the first brick below
            case ENGLISH -> new Course(List.of(brick.width()), brick.length() + joint / 2 - brick.width() / 2);
            // the end's middle, which is a brick, a joint and half an end along, over the first brick's middle
            case FLEMISH -> new Course(List.of(brick.length(), brick.width()),
                    brick.length() / 2 - (lengthwise + brick.width() / 2));
            // the end's middle, three bricks and their joints and half an end along, over the second brick's middle
            case THREE_AND_ONE -> new Course(
                    List.of(brick.length(), brick.length(), brick.length(), brick.width()),
                    lengthwise + brick.length() / 2 - (3 * lengthwise + brick.width() / 2));
        };
    }
}

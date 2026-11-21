package com.example.fxsuite.texture.brick;

import java.util.List;

/**
 * One course of a wall as it is laid: the lengths of the faces its bricks show, one after another till they come
 * round again, in centimetres; and how far along the wall the whole course is shifted, so its joints do not stand
 * over those of the course below.
 */
public record Course(List<Double> faces, double shifted) {

    public Course {
        faces = List.copyOf(faces);
        if (faces.isEmpty()) {
            throw new IllegalArgumentException("a course is laid of bricks");
        }
    }

    /** How far along it is before its faces come round again, a joint of {@code joint} after each. */
    public double repeat(final double joint) {
        return faces.stream().mapToDouble(face -> face + joint).sum();
    }

    /** How far from where its faces begin to come round face {@code which} begins, a joint after each before it. */
    public double begins(final int which, final double joint) {
        return faces.subList(0, which).stream().mapToDouble(face -> face + joint).sum();
    }
}

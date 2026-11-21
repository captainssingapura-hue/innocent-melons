package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * What a surface's terms are given at each point drawn, the same in the shader and in Java's working: where on the
 * thing the point is, in the thing's own frame, and how much of the surface a pixel covers there. The names are said
 * here once, for the terms that read them, the shader that declares them and the sample that gives them.
 */
public record Seen() {

    private static final String AT = "at";
    private static final String COVERS = "covers";

    /** Where on the thing the point is, in the thing's own frame. */
    public Point at() {
        return new Point(new Given(AT, 3));
    }

    /** How much of the surface a pixel covers at the point, in centimetres: little near, more far off. */
    public Real covers() {
        return new Real(new Given(COVERS, 1));
    }

    /** What the shader calls where the point is. */
    public String atName() {
        return AT;
    }

    /** What the shader calls how much a pixel covers. */
    public String coversName() {
        return COVERS;
    }

    /** A sample at {@code at} on the thing, a pixel covering {@code covers} centimetres there. */
    public Sample sample(final Vec3 at, final double covers) {
        return new Sample().with(AT, at.x(), at.y(), at.z()).with(COVERS, covers);
    }
}

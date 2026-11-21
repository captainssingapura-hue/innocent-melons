package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/** How a muscle's sections are written down ({@link Section}), each on a bone, as the bone is drawn. */
public record Sections() implements StatelessFunctionalObject {

    public static final Sections INSTANCE = new Sections();

    /** At ({@code x}, {@code y}, {@code z}) on {@code bone}, {@code wide} by {@code deep}, facing ({@code fx}, ...). */
    public Section at(final Bone bone, final double x, final double y, final double z, final double wide,
                      final double deep, final double fx, final double fy, final double fz) {
        return new Section(new Anchor(bone, new V3(x, y, z)), wide, deep, new V3(fx, fy, fz).unit());
    }
}

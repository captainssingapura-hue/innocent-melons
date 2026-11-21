package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor digitorum superficialis: from the humerus's inner epicondyle and the radius, under the wrist's flexors,
 * its tendons through the wrist to the fingers.
 */
public record FlexorDigitorumSuperficialis() implements Muscle {

    public static final FlexorDigitorumSuperficialis INSTANCE = new FlexorDigitorumSuperficialis();

    @Override
    public String name() {
        return "flexor digitorum superficialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -2.714, -29.6, -0.087, 1.0, 0.6, -0.3, 0, 1),
                s.at(Ulna.INSTANCE, -0.6, -9, 0.85, 3.6, 1.7, 0, 0, 1),
                s.at(Ulna.INSTANCE, -0.6, -18, 1, 2.9, 1.4, 0, 0, 1),
                s.at(Radius.INSTANCE, 0, -23.8, 0.75, 2.0, 0.5, 0, 0, 1));
    }
}

package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor digitorum profundus: the deepest of the forearm's front, on the ulna, its tendons through the wrist to the
 * fingertips.
 */
public record FlexorDigitorumProfundus() implements Muscle {

    public static final FlexorDigitorumProfundus INSTANCE = new FlexorDigitorumProfundus();

    @Override
    public String name() {
        return "flexor digitorum profundus";
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
                s.at(Ulna.INSTANCE, -1, -6, 0.1, 2.4, 1.4, 0, 0, 1),
                s.at(Ulna.INSTANCE, -0.6, -16, 0.2, 2.4, 1.2, 0, 0, 1),
                s.at(Radius.INSTANCE, -0.2, -23.6, 0.4, 1.8, 0.4, 0, 0, 1));
    }
}

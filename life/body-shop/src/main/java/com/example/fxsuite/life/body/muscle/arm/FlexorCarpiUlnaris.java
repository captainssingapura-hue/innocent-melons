package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor carpi ulnaris: from the humerus's inner epicondyle and the olecranon, down the forearm's inner side along
 * the ulna, to the wrist's little-finger side.
 */
public record FlexorCarpiUlnaris() implements Muscle {

    public static final FlexorCarpiUlnaris INSTANCE = new FlexorCarpiUlnaris();

    @Override
    public String name() {
        return "flexor carpi ulnaris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -2.8, -29.9, -0.3, 0.8, 0.6, -1, 0, 0),
                s.at(Ulna.INSTANCE, -1.679, -6, 0.05, 2.2, 1.7, -0.8, 0, 0.5),
                s.at(Ulna.INSTANCE, -2.091, -16, 0.082, 1.4, 1.2, -0.8, 0, 0.5),
                s.at(Ulna.INSTANCE, -2.188, -23.4, 0.268, 0.6, 0.5, -0.8, 0, 0.5));
    }
}

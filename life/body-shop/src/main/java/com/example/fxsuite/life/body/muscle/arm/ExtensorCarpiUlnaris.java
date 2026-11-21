package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor carpi ulnaris: from the humerus's outer epicondyle, down the back of the forearm along the ulna, to the
 * wrist's inner side.
 */
public record ExtensorCarpiUlnaris() implements Muscle {

    public static final ExtensorCarpiUlnaris INSTANCE = new ExtensorCarpiUlnaris();

    @Override
    public String name() {
        return "extensor carpi ulnaris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.288, -29.9, -0.676, 0.8, 0.5, 0.5, 0, -1),
                s.at(Ulna.INSTANCE, -0.399, -7, -1.43, 1.9, 1.4, -0.3, 0, -1),
                s.at(Ulna.INSTANCE, -1.357, -17, -1.263, 1.4, 1.0, -0.5, 0, -0.9),
                s.at(Ulna.INSTANCE, -2.159, -24, -0.959, 0.6, 0.4, -0.7, 0, -0.7));
    }
}

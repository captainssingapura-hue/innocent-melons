package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor digitorum: from the humerus's outer epicondyle, down the middle of the back of the forearm, its tendons
 * over the back of the wrist to the fingers.
 */
public record ExtensorDigitorum() implements Muscle {

    public static final ExtensorDigitorum INSTANCE = new ExtensorDigitorum();

    @Override
    public String name() {
        return "extensor digitorum";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.446, -29.7, -0.343, 1.0, 0.6, 0.6, 0, -1),
                s.at(Ulna.INSTANCE, 0.6, -6, -1.65, 2.6, 1.9, 0, 0, -1),
                s.at(Ulna.INSTANCE, 0.3, -15, -1.5, 2.2, 1.2, 0, 0, -1),
                s.at(Radius.INSTANCE, 0.6, -23.6, -1.2, 1.6, 0.4, 0, 0, -1));
    }
}

package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor digiti minimi: from the humerus's outer epicondyle, beside the extensor digitorum, its tendon over the
 * back of the wrist to the little finger.
 */
public record ExtensorDigitiMinimi() implements Muscle {

    public static final ExtensorDigitiMinimi INSTANCE = new ExtensorDigitiMinimi();

    @Override
    public String name() {
        return "extensor digiti minimi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.311, -29.8, -0.621, 0.6, 0.4, 0.5, 0, -1),
                s.at(Ulna.INSTANCE, -0.6, -10, -2.05, 1.0, 0.7, 0, 0, -1),
                s.at(Ulna.INSTANCE, -1.6, -23.8, -1.45, 0.4, 0.3, 0, 0, -1),
                s.at(FifthMetacarpal.INSTANCE, -2.4, -6, -0.65, 0.4, 0.3, 0, 0, -1));
    }
}

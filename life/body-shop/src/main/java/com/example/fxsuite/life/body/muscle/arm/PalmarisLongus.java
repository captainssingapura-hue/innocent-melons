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
 * The palmaris longus: from the humerus's inner epicondyle, a small belly and a long thin tendon down the middle of the
 * front of the forearm to the palm.
 */
public record PalmarisLongus() implements Muscle {

    public static final PalmarisLongus INSTANCE = new PalmarisLongus();

    @Override
    public String name() {
        return "palmaris longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -2.828, -30, 0.561, 0.6, 0.5, -0.3, 0, 1),
                s.at(Ulna.INSTANCE, -0.9, -6, 2.15, 1.2, 0.9, 0, 0, 1),
                s.at(Ulna.INSTANCE, -0.4, -12, 2.55, 0.4, 0.3, 0, 0, 1),
                s.at(Radius.INSTANCE, 0.2, -23.8, 1.45, 0.5, 0.3, 0, 0, 1));
    }
}

package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.bone.hand.LittleFingerProximalPhalanx;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor digiti minimi brevis: beside the abductor, from the hamate's hook to the little finger's first phalanx.
 */
public record FlexorDigitiMinimiBrevis() implements Muscle {

    public static final FlexorDigitiMinimiBrevis INSTANCE = new FlexorDigitiMinimiBrevis();

    @Override
    public String name() {
        return "flexor digiti minimi brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Hamate.INSTANCE, -1.2, -2.8, 1.3, 0.8, 0.6, 0, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -2.085, -6, 1.017, 1.0, 0.8, -0.3, 0, 1),
                s.at(LittleFingerProximalPhalanx.INSTANCE, -2.5, -9.4, 0.7, 0.4, 0.4, 0, 0, 1));
    }
}

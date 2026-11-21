package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.LittleFingerProximalPhalanx;
import com.example.fxsuite.life.body.bone.hand.Pisiform;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The abductor digiti minimi: the heel of the palm's edge, from the pisiform to the little finger's first phalanx.
 */
public record AbductorDigitiMinimiManus() implements Muscle {

    public static final AbductorDigitiMinimiManus INSTANCE = new AbductorDigitiMinimiManus();

    @Override
    public String name() {
        return "abductor digiti minimi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Pisiform.INSTANCE, -1.513, -1.8, 1.314, 1.0, 0.6, -1, 0, 0.3),
                s.at(FifthMetacarpal.INSTANCE, -2.521, -6, 0.656, 1.2, 1.0, -1, 0, 0.3),
                s.at(LittleFingerProximalPhalanx.INSTANCE, -2.708, -9.4, 0.543, 0.5, 0.4, -1, 0, 0.3));
    }
}

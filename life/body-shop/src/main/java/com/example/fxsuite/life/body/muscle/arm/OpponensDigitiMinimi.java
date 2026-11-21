package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The opponens digiti minimi: under the other two, from the hamate's hook along the fifth metacarpal.
 */
public record OpponensDigitiMinimi() implements Muscle {

    public static final OpponensDigitiMinimi INSTANCE = new OpponensDigitiMinimi();

    @Override
    public String name() {
        return "opponens digiti minimi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Hamate.INSTANCE, -1.041, -3, 0.946, 0.8, 0.6, -0.5, 0, 0.8),
                s.at(FifthMetacarpal.INSTANCE, -2.241, -6.4, 0.346, 1.0, 0.6, -0.5, 0, 0.8));
    }
}

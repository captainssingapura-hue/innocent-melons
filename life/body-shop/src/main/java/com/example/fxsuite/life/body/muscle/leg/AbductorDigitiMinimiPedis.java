package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The abductor digiti minimi of the foot: along the outer edge of the sole, from the heel bone to the little toe.
 */
public record AbductorDigitiMinimiPedis() implements Muscle {

    public static final AbductorDigitiMinimiPedis INSTANCE = new AbductorDigitiMinimiPedis();

    @Override
    public String name() {
        return "abductor digiti minimi, of the foot";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Calcaneus.INSTANCE, 1.136, -5.014, -1, 1.4, 1.0, 1, -0.4, 0),
                s.at(FifthMetatarsal.INSTANCE, 2.936, -5.014, 6, 1.4, 1.0, 1, -0.4, 0),
                s.at(FifthMetatarsal.INSTANCE, 3.368, -5.307, 10.6, 0.8, 0.5, 1, -0.4, 0));
    }
}

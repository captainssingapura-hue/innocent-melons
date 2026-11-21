package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The tibialis anterior: from the tibia's outer face, beside its crest, down the front of the shin, its tendon over the
 * front of the ankle to the inner side of the foot, the first metatarsal's base.
 */
public record TibialisAnterior() implements Muscle {

    public static final TibialisAnterior INSTANCE = new TibialisAnterior();

    @Override
    public String name() {
        return "tibialis anterior";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 1.587, -6, 0.774, 2.2, 1.4, 0.5, 0, 1),
                s.at(Tibia.INSTANCE, 1.613, -16, 1.003, 2.9, 2.4, 0.4, 0, 0.9),
                s.at(Tibia.INSTANCE, 1.163, -28, 1.314, 1.9, 1.4, 0.2, 0, 1),
                s.at(Tibia.INSTANCE, 0.3, -36, 1.75, 1.1, 0.7, 0, 0, 1),
                s.at(Tibia.INSTANCE, -0.8, -40.5, 2.1, 1.1, 0.6, 0, 0, 1),
                s.at(FirstMetatarsal.INSTANCE, -1.4, 0.113, 2.914, 1.0, 0.6, 0, 1, 0.3),
                s.at(FirstMetatarsal.INSTANCE, -2.267, -2.733, 6.333, 0.8, 0.4, -0.6, 0.6, 0.3));
    }
}

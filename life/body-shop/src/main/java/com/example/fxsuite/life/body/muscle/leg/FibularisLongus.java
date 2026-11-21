package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fibularis longus: from the fibula's head and upper shaft, down the outer side of the leg, its tendon behind the
 * outer ankle bone and under the foot.
 */
public record FibularisLongus() implements Muscle {

    public static final FibularisLongus INSTANCE = new FibularisLongus();

    @Override
    public String name() {
        return "fibularis longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 3.1, -5.5, -1.2, 1.6, 1.0, 1, 0, 0),
                s.at(Tibia.INSTANCE, 3.2, -16, -0.8, 2.5, 1.8, 1, 0, 0),
                s.at(Tibia.INSTANCE, 3.35, -28, -1, 1.6, 1.1, 1, 0, 0),
                s.at(Tibia.INSTANCE, 3.113, -38, -1.714, 0.8, 0.6, 1, 0, -0.3),
                s.at(FifthMetatarsal.INSTANCE, 2.713, -0.4, -1.714, 0.8, 0.6, 1, 0, -0.3),
                s.at(FifthMetatarsal.INSTANCE, 2.513, -2.914, 1, 0.8, 0.6, 1, -0.3, 0),
                s.at(FifthMetatarsal.INSTANCE, 1.871, -4.186, 4, 0.7, 0.5, 0.6, -1, 0));
    }
}

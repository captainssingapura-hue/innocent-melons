package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fibularis brevis: from the fibula's lower shaft, under the longus, its tendon behind the outer ankle bone to the
 * base of the fifth metatarsal.
 */
public record FibularisBrevis() implements Muscle {

    public static final FibularisBrevis INSTANCE = new FibularisBrevis();

    @Override
    public String name() {
        return "fibularis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 3.5, -24, -1, 1.6, 1.0, 1, 0, 0),
                s.at(Tibia.INSTANCE, 3.4, -34, -1.4, 1.4, 0.8, 1, 0, 0),
                s.at(Tibia.INSTANCE, 3.108, -39.5, -1.943, 0.6, 0.4, 1, 0, -0.3),
                s.at(FifthMetatarsal.INSTANCE, 2.608, -0.8, -1.743, 0.6, 0.4, 1, 0, -0.3),
                s.at(FifthMetatarsal.INSTANCE, 2.6, -3.2, 2, 0.6, 0.4, 1, 0, 0),
                s.at(FifthMetatarsal.INSTANCE, 2.4, -3.8, 5, 0.8, 0.4, 1, 0, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fibularis tertius: the lowest part of the toes' long extensor, from the front of the fibula low down, its tendon
 * over the ankle to the back of the fifth metatarsal's base.
 */
public record FibularisTertius() implements Muscle {

    public static final FibularisTertius INSTANCE = new FibularisTertius();

    @Override
    public String name() {
        return "fibularis tertius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 3.051, -30, 0.229, 1.0, 0.8, 0.4, 0, 1),
                s.at(Tibia.INSTANCE, 2.4, -40.5, 1.8, 0.6, 0.4, 0, 0, 1),
                s.at(FifthMetatarsal.INSTANCE, 2.4, -3.15, 6, 0.6, 0.3, 0, 1, 0));
    }
}

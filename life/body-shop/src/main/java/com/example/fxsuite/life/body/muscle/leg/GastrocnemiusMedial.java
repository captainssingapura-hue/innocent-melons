package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gastrocnemius's medial head: from behind the femur's inner condyle, the inner bulk of the calf, reaching lower
 * than the outer, into the Achilles tendon.
 */
public record GastrocnemiusMedial() implements Muscle {

    public static final GastrocnemiusMedial INSTANCE = new GastrocnemiusMedial();

    @Override
    public String name() {
        return "gastrocnemius, medial head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, -2.2, -38.6, -2.9, 2.0, 1.0, 0, 0, -1),
                s.at(Tibia.INSTANCE, -1.483, -4, -3.011, 4.3, 2.9, -0.3, 0, -1),
                s.at(Tibia.INSTANCE, -1.031, -11, -3.829, 5.0, 3.6, -0.4, 0, -1),
                s.at(Tibia.INSTANCE, -0.633, -18, -3.867, 3.6, 1.7, -0.2, 0, -1),
                s.at(Tibia.INSTANCE, -0.2, -22, -3.7, 2.6, 0.4, 0, 0, -1));
    }
}

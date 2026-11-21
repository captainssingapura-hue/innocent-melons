package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gastrocnemius's lateral head: from behind the femur's outer condyle, the outer bulk of the calf, into the
 * Achilles tendon.
 */
public record GastrocnemiusLateral() implements Muscle {

    public static final GastrocnemiusLateral INSTANCE = new GastrocnemiusLateral();

    @Override
    public String name() {
        return "gastrocnemius, lateral head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 2.2, -38.6, -2.9, 1.8, 1.0, 0, 0, -1),
                s.at(Tibia.INSTANCE, 1.755, -5, -3.051, 3.6, 2.4, 0.3, 0, -1),
                s.at(Tibia.INSTANCE, 1.361, -10, -3.454, 4.1, 2.9, 0.4, 0, -1),
                s.at(Tibia.INSTANCE, 0.682, -16, -3.912, 2.9, 1.2, 0.2, 0, -1),
                s.at(Tibia.INSTANCE, 0.3, -20, -3.7, 2.2, 0.4, 0, 0, -1));
    }
}

package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.muscle.arm.ArmMuscles;
import com.example.fxsuite.life.body.muscle.head.HeadMuscles;
import com.example.fxsuite.life.body.muscle.leg.LegMuscles;
import com.example.fxsuite.life.body.muscle.trunk.TrunkMuscles;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The body's muscles, region by region, from the head down: the head's and the neck's, the trunk's, the arm's, the
 * leg's.
 */
public record Musculature() implements StatelessFunctionalObject {

    public static final Musculature INSTANCE = new Musculature();

    /** Every muscle, as on the right. */
    public List<Muscle> muscles() {
        final List<Muscle> all = new ArrayList<>(HeadMuscles.INSTANCE.muscles());
        all.addAll(TrunkMuscles.INSTANCE.muscles());
        all.addAll(ArmMuscles.INSTANCE.muscles());
        all.addAll(LegMuscles.INSTANCE.muscles());
        return List.copyOf(all);
    }
}

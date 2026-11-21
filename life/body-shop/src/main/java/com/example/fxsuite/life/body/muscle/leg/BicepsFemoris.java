package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Fibula;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The biceps femoris, its long head: from the ischial tuberosity, down the back and outer side of the thigh, to the
 * head of the fibula; the outer hamstring.
 */
public record BicepsFemoris() implements Muscle {

    public static final BicepsFemoris INSTANCE = new BicepsFemoris();

    @Override
    public String name() {
        return "biceps femoris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 5.6, -9, -3.1, 1.4, 1.0, 0, 0, -1),
                s.at(Femur.INSTANCE, 2.396, -14, -3.68, 3.9, 3.1, 0.2, 0, -1),
                s.at(Femur.INSTANCE, 3.374, -26, -3.414, 4.7, 3.4, 0.5, 0, -0.9),
                s.at(Femur.INSTANCE, 3.682, -36, -2.59, 3.1, 2.1, 0.9, 0, -0.5),
                s.at(Fibula.INSTANCE, 3.113, -4, -1.714, 1.0, 0.6, 1, 0, -0.3));
    }
}

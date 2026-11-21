package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Patella;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus femoris: from the anterior inferior iliac spine, straight down the front of the thigh over the vastus
 * intermedius, to the top of the kneecap; the one of the quadriceps that crosses the hip.
 */
public record RectusFemoris() implements Muscle {

    public static final RectusFemoris INSTANCE = new RectusFemoris();

    @Override
    public String name() {
        return "rectus femoris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 10.4, 2.6, 4.7, 1.0, 0.6, 0, 0, 1),
                s.at(Femur.INSTANCE, 2.4, -6, 3.95, 3.2, 2.5, 0, 0, 1),
                s.at(Femur.INSTANCE, 2.4, -17, 4.1, 5.1, 3.4, 0, 0, 1),
                s.at(Femur.INSTANCE, 1.6, -28, 3.95, 4.1, 2.5, 0, 0, 1),
                s.at(Femur.INSTANCE, 0.6, -35, 4.25, 3.4, 0.9, 0, 0, 1),
                s.at(Patella.INSTANCE, 0, -37.4, 4.55, 3.4, 0.5, 0, 0, 1));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The sartorius: from the anterior superior iliac spine, a strap across the front of the thigh and down its inner side,
 * behind the knee's inner side, to the tibia's upper inner face; the body's longest muscle.
 */
public record Sartorius() implements Muscle {

    public static final Sartorius INSTANCE = new Sartorius();

    @Override
    public String name() {
        return "sartorius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 11.928, 6.4, 5.761, 1.2, 0.5, 0.3, 0, 1),
                s.at(Femur.INSTANCE, 3, -8, 6.05, 3.3, 1.1, 0, 0, 1),
                s.at(Femur.INSTANCE, 0.479, -20, 5.526, 3.3, 1.1, -0.5, 0, 0.85),
                s.at(Femur.INSTANCE, -2.497, -31, 3.377, 2.9, 1.1, -0.9, 0, 0.4),
                s.at(Femur.INSTANCE, -4.35, -38.5, -0.4, 2.0, 0.9, -1, 0, 0),
                s.at(Tibia.INSTANCE, -2.804, -5, -0.039, 1.3, 0.4, -1, 0, 0.2),
                s.at(Tibia.INSTANCE, -2.294, -7.4, 0.894, 1.0, 0.3, -0.7, 0, 0.7));
    }
}

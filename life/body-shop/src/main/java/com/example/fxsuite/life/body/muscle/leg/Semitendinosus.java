package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semitendinosus: from the ischial tuberosity, down the back of the thigh's inner side, its long tendon round
 * behind the knee's inner side to the tibia's upper inner face.
 */
public record Semitendinosus() implements Muscle {

    public static final Semitendinosus INSTANCE = new Semitendinosus();

    @Override
    public String name() {
        return "semitendinosus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 5.2, -9.2, -2.7, 1.3, 1.0, 0, 0, -1),
                s.at(Femur.INSTANCE, 0.939, -14, -4.107, 3.2, 2.8, -0.1, 0, -1),
                s.at(Femur.INSTANCE, 0.324, -26, -3.956, 3.0, 2.5, -0.1, 0, -1),
                s.at(Femur.INSTANCE, -1.256, -34, -3.621, 1.2, 1.0, -0.3, 0, -1),
                s.at(Femur.INSTANCE, -3.388, -40, -2.388, 0.8, 0.6, -0.7, 0, -0.7),
                s.at(Tibia.INSTANCE, -2.65, -5.5, -0.6, 0.8, 0.5, -1, 0, 0),
                s.at(Tibia.INSTANCE, -2.094, -7.6, 0.894, 0.8, 0.3, -0.7, 0, 0.7));
    }
}

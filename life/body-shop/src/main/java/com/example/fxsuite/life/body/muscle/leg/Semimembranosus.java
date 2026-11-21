package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semimembranosus: from the ischial tuberosity, a flat tendon broadening low into a thick belly under the
 * semitendinosus, to the back of the tibia's inner condyle.
 */
public record Semimembranosus() implements Muscle {

    public static final Semimembranosus INSTANCE = new Semimembranosus();

    @Override
    public String name() {
        return "semimembranosus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 5.9, -8.2, -2, 1.5, 0.8, 0, 0, -1),
                s.at(Femur.INSTANCE, 0.757, -18, -2.816, 3.2, 1.6, -0.2, 0, -1),
                s.at(Femur.INSTANCE, -0.069, -30, -2.622, 5.2, 3.4, -0.4, 0, -1),
                s.at(Femur.INSTANCE, -2.06, -38.4, -2.22, 2.6, 1.6, -0.8, 0, -0.6),
                s.at(Tibia.INSTANCE, -2.613, -3, -1.114, 1.2, 0.6, -1, 0, -0.3));
    }
}

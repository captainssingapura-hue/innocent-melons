package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor digitorum longus: from the back of the tibia, down behind the inner ankle bone, under the foot to the
 * toes.
 */
public record FlexorDigitorumLongus() implements Muscle {

    public static final FlexorDigitorumLongus INSTANCE = new FlexorDigitorumLongus();

    @Override
    public String name() {
        return "flexor digitorum longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, -1.056, -12, -0.921, 1.4, 1.0, -0.3, 0, -1),
                s.at(Tibia.INSTANCE, -1.285, -30, -1.417, 1.2, 0.8, -0.3, 0, -1),
                s.at(Tibia.INSTANCE, -2.421, -41, -1.711, 0.5, 0.4, -1, 0, -0.5),
                s.at(Navicular.INSTANCE, -1.6, -4.6, 4, 0.6, 0.4, 0, -1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.8, -5.25, 11, 1.6, 0.3, 0, -1, 0));
    }
}

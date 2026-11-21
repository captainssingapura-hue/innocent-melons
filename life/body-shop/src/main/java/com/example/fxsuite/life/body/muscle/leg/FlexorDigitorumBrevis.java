package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor digitorum brevis: down the middle of the sole, from the heel bone toward the four lesser toes.
 */
public record FlexorDigitorumBrevis() implements Muscle {

    public static final FlexorDigitorumBrevis INSTANCE = new FlexorDigitorumBrevis();

    @Override
    public String name() {
        return "flexor digitorum brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Calcaneus.INSTANCE, 0.2, -5.2, -1.2, 2.0, 1.0, 0, -1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.6, -4.3, 6, 3.0, 1.2, 0, -1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.6, -5.35, 10.5, 2.6, 0.5, 0, -1, 0));
    }
}

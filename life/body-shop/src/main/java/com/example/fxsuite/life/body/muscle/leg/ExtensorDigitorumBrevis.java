package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor digitorum brevis: a small belly on the top of the foot's outer side, from the heel bone forward toward
 * the toes.
 */
public record ExtensorDigitorumBrevis() implements Muscle {

    public static final ExtensorDigitorumBrevis INSTANCE = new ExtensorDigitorumBrevis();

    @Override
    public String name() {
        return "extensor digitorum brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Calcaneus.INSTANCE, 1.722, -2.592, 2.2, 1.8, 0.8, 0.2, 1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 1.141, -2.294, 7, 2.2, 0.6, 0.2, 1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.6, -3.75, 10.5, 2.4, 0.3, 0, 1, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor hallucis brevis: the extensor digitorum brevis's inner slip, from the heel bone forward across the top
 * of the foot to the great toe.
 */
public record ExtensorHallucisBrevis() implements Muscle {

    public static final ExtensorHallucisBrevis INSTANCE = new ExtensorHallucisBrevis();

    @Override
    public String name() {
        return "extensor hallucis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Calcaneus.INSTANCE, 1.4, -2.25, 2.6, 1.0, 0.5, 0, 1, 0),
                s.at(FirstMetatarsal.INSTANCE, -1.2, -2.8, 8, 0.8, 0.4, 0, 1, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.2, -3.95, 12.6, 0.4, 0.3, 0, 1, 0));
    }
}

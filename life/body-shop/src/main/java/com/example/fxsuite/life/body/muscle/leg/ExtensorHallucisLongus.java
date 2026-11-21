package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor hallucis longus: from the middle of the fibula's front, between the tibialis anterior and the toes' long
 * extensor, its tendon over the ankle and along the top of the foot to the great toe.
 */
public record ExtensorHallucisLongus() implements Muscle {

    public static final ExtensorHallucisLongus INSTANCE = new ExtensorHallucisLongus();

    @Override
    public String name() {
        return "extensor hallucis longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 2.451, -20, 0.229, 1.0, 0.8, 0.4, 0, 1),
                s.at(Tibia.INSTANCE, 1, -38, 1.75, 0.6, 0.5, 0, 0, 1),
                s.at(FirstMetatarsal.INSTANCE, -1.6, -1.75, 6, 0.6, 0.3, 0, 1, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.4, -3.55, 12.6, 0.6, 0.3, 0, 1, 0));
    }
}

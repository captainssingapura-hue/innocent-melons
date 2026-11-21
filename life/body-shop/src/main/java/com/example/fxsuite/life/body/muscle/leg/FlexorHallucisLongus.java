package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor hallucis longus: from the back of the fibula, down behind the ankle, under the heel bone's shelf, along
 * the sole to the great toe.
 */
public record FlexorHallucisLongus() implements Muscle {

    public static final FlexorHallucisLongus INSTANCE = new FlexorHallucisLongus();

    @Override
    public String name() {
        return "flexor hallucis longus";
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
                s.at(Tibia.INSTANCE, 2.399, -18, -1.33, 1.8, 1.4, 0.3, 0, -1),
                s.at(Tibia.INSTANCE, 1, -34, -1.6, 1.6, 1.2, 0, 0, -1),
                s.at(Tibia.INSTANCE, -0.8, -41, -2.2, 0.5, 0.4, 0, 0, -1),
                s.at(Calcaneus.INSTANCE, -1.421, -2.911, 0.4, 0.5, 0.4, -1, -0.5, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.4, -5.6, 11, 0.6, 0.4, 0, -1, 0));
    }
}

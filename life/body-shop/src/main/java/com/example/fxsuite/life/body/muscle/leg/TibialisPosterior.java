package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The tibialis posterior: the deepest of the calf, from between the tibia and the fibula behind, its tendon behind the
 * inner ankle bone to the navicular.
 */
public record TibialisPosterior() implements Muscle {

    public static final TibialisPosterior INSTANCE = new TibialisPosterior();

    @Override
    public String name() {
        return "tibialis posterior";
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
                s.at(Tibia.INSTANCE, 1, -8, -0.9, 1.6, 1.4, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.6, -24, -0.9, 1.8, 1.4, 0, 0, -1),
                s.at(Tibia.INSTANCE, -1.488, -38, -1.176, 0.6, 0.5, -0.5, 0, -1),
                s.at(Tibia.INSTANCE, -2.176, -41, -1.488, 0.6, 0.5, -1, 0, -0.5),
                s.at(Navicular.INSTANCE, -2.4, -2.8, 4.8, 0.6, 0.4, -1, 0, 0));
    }
}

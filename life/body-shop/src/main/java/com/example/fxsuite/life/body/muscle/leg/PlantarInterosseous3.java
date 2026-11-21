package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's third plantar interosseous: under the fifth metatarsal, to the little toe's great-toe side.
 */
public record PlantarInterosseous3() implements Muscle {

    public static final PlantarInterosseous3 INSTANCE = new PlantarInterosseous3();

    @Override
    public String name() {
        return "third plantar interosseous, of the foot";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(FifthMetatarsal.INSTANCE, 2, -4.1, 7.6, 0.6, 0.6, 0, -1, 0),
                s.at(FifthMetatarsal.INSTANCE, 2.6, -4.65, 11.2, 0.4, 0.3, 0, -1, 0));
    }
}

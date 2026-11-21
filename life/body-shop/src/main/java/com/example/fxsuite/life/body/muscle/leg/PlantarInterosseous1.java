package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's first plantar interosseous: under the third metatarsal, to the third toe's great-toe side.
 */
public record PlantarInterosseous1() implements Muscle {

    public static final PlantarInterosseous1 INSTANCE = new PlantarInterosseous1();

    @Override
    public String name() {
        return "first plantar interosseous, of the foot";
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
                s.at(ThirdMetatarsal.INSTANCE, 0.2, -4.1, 8, 0.6, 0.6, 0, -1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.4, -4.65, 12.4, 0.4, 0.3, 0, -1, 0));
    }
}

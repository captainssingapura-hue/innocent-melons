package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's third dorsal interosseous: between the third and the fourth metatarsals, to the third toe's little-toe
 * side.
 */
public record DorsalInterosseousPedis3() implements Muscle {

    public static final DorsalInterosseousPedis3 INSTANCE = new DorsalInterosseousPedis3();

    @Override
    public String name() {
        return "third dorsal interosseous, of the foot";
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
                s.at(ThirdMetatarsal.INSTANCE, 1, -3.55, 8.8, 0.7, 0.7, 0, 1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.8, -3.95, 12.6, 0.4, 0.3, 0, 1, 0));
    }
}

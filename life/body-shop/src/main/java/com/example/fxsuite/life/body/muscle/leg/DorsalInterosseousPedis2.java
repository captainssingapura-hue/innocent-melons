package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's second dorsal interosseous: between the second and the third metatarsals, to the second toe's little-toe
 * side.
 */
public record DorsalInterosseousPedis2() implements Muscle {

    public static final DorsalInterosseousPedis2 INSTANCE = new DorsalInterosseousPedis2();

    @Override
    public String name() {
        return "second dorsal interosseous, of the foot";
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
                s.at(SecondMetatarsal.INSTANCE, 0, -3.35, 9, 0.7, 0.7, 0, 1, 0),
                s.at(SecondMetatarsal.INSTANCE, -0.4, -3.75, 12.6, 0.4, 0.3, 0, 1, 0));
    }
}

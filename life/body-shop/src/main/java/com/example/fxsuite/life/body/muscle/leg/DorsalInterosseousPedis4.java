package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's fourth dorsal interosseous: between the fourth and the fifth metatarsals, to the fourth toe's little-toe
 * side.
 */
public record DorsalInterosseousPedis4() implements Muscle {

    public static final DorsalInterosseousPedis4 INSTANCE = new DorsalInterosseousPedis4();

    @Override
    public String name() {
        return "fourth dorsal interosseous, of the foot";
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
                s.at(FourthMetatarsal.INSTANCE, 2, -3.75, 8.4, 0.7, 0.7, 0, 1, 0),
                s.at(FourthMetatarsal.INSTANCE, 1.8, -4.15, 12, 0.4, 0.3, 0, 1, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's first dorsal interosseous: between the first and the second metatarsals on the top of the foot, to the
 * second toe's great-toe side.
 */
public record DorsalInterosseousPedis1() implements Muscle {

    public static final DorsalInterosseousPedis1 INSTANCE = new DorsalInterosseousPedis1();

    @Override
    public String name() {
        return "first dorsal interosseous, of the foot";
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
                s.at(FirstMetatarsal.INSTANCE, -1.4, -3.35, 9, 0.7, 0.7, 0, 1, 0),
                s.at(SecondMetatarsal.INSTANCE, -0.8, -3.75, 12.4, 0.4, 0.3, 0, 1, 0));
    }
}

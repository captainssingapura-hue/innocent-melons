package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's second lumbrical: from the long flexor's tendons, to the third toe's great-toe side.
 */
public record LumbricalPedis2() implements Muscle {

    public static final LumbricalPedis2 INSTANCE = new LumbricalPedis2();

    @Override
    public String name() {
        return "second lumbrical, of the foot";
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
                s.at(ThirdMetatarsal.INSTANCE, 0.4, -4.95, 7.4, 0.6, 0.5, 0, -1, 0),
                s.at(ThirdMetatarsal.INSTANCE, 0.2, -5.05, 12.8, 0.4, 0.3, 0, -1, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's fourth lumbrical: from the long flexor's tendons, to the little toe's great-toe side.
 */
public record LumbricalPedis4() implements Muscle {

    public static final LumbricalPedis4 INSTANCE = new LumbricalPedis4();

    @Override
    public String name() {
        return "fourth lumbrical, of the foot";
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
                s.at(FifthMetatarsal.INSTANCE, 2.2, -4.95, 7, 0.6, 0.5, 0, -1, 0),
                s.at(FifthMetatarsal.INSTANCE, 2.4, -5.05, 11.6, 0.4, 0.3, 0, -1, 0));
    }
}

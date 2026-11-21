package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's third lumbrical: from the long flexor's tendons, to the fourth toe's great-toe side.
 */
public record LumbricalPedis3() implements Muscle {

    public static final LumbricalPedis3 INSTANCE = new LumbricalPedis3();

    @Override
    public String name() {
        return "third lumbrical, of the foot";
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
                s.at(FourthMetatarsal.INSTANCE, 1.4, -4.95, 7.2, 0.6, 0.5, 0, -1, 0),
                s.at(FourthMetatarsal.INSTANCE, 1.2, -5.05, 12.2, 0.4, 0.3, 0, -1, 0));
    }
}

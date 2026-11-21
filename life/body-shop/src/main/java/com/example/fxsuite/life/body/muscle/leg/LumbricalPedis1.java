package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's first lumbrical: from the long flexor's tendon to the second toe, to that toe's great-toe side.
 */
public record LumbricalPedis1() implements Muscle {

    public static final LumbricalPedis1 INSTANCE = new LumbricalPedis1();

    @Override
    public String name() {
        return "first lumbrical, of the foot";
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
                s.at(SecondMetatarsal.INSTANCE, -0.6, -4.95, 7, 0.6, 0.5, 0, -1, 0),
                s.at(SecondMetatarsal.INSTANCE, -0.8, -4.85, 12.6, 0.4, 0.3, 0, -1, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The foot's flexor digiti minimi brevis: under the fifth metatarsal, from its base to the little toe's.
 */
public record FlexorDigitiMinimiBrevisPedis() implements Muscle {

    public static final FlexorDigitiMinimiBrevisPedis INSTANCE = new FlexorDigitiMinimiBrevisPedis();

    @Override
    public String name() {
        return "flexor digiti minimi brevis, of the foot";
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
                s.at(FifthMetatarsal.INSTANCE, 2.6, -4.3, 6.6, 1.0, 0.6, 0, -1, 0),
                s.at(FifthMetatarsal.INSTANCE, 3, -5.15, 11.2, 0.8, 0.5, 0, -1, 0));
    }
}

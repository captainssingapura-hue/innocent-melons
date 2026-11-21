package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The adductor hallucis, its oblique head: from the bases of the middle metatarsals, forward and in to the great toe's
 * base.
 */
public record AdductorHallucis() implements Muscle {

    public static final AdductorHallucis INSTANCE = new AdductorHallucis();

    @Override
    public String name() {
        return "adductor hallucis";
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
                s.at(ThirdMetatarsal.INSTANCE, 0.4, -4.2, 7, 1.6, 0.8, 0, -1, 0),
                s.at(FirstMetatarsal.INSTANCE, -1.6, -5.1, 12.4, 1.0, 0.6, 0, -1, 0));
    }
}

package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor hallucis brevis: from the cuboid and the lateral cuneiform's underside, forward under the first metatarsal
 * in two bellies to the great toe's base, its sesamoids in its tendons.
 */
public record FlexorHallucisBrevis() implements Muscle {

    public static final FlexorHallucisBrevis INSTANCE = new FlexorHallucisBrevis();

    @Override
    public String name() {
        return "flexor hallucis brevis";
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
                s.at(Navicular.INSTANCE, -1.2, -4.1, 5.6, 1.0, 0.6, 0, -1, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.4, -5.1, 11, 1.6, 1.0, 0, -1, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.4, -5.35, 13.2, 0.8, 0.5, 0, -1, 0));
    }
}

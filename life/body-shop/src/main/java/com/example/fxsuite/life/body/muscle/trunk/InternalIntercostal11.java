package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.EleventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the eleventh space: from the angles round to the front, between the eleventh and the
 * twelfth ribs, its fibres running down and back.
 */
public record InternalIntercostal11() implements Muscle {

    public static final InternalIntercostal11 INSTANCE = new InternalIntercostal11();

    @Override
    public String name() {
        return "internal intercostal, eleventh space";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(EleventhRib.INSTANCE, 2.971, 27.43, -7.002, 2.04, 0.40, 0.347, 0.000, -0.938),
                s.at(EleventhRib.INSTANCE, 7.614, 26.25, -6.153, 1.44, 0.40, 0.729, 0.000, -0.685),
                s.at(EleventhRib.INSTANCE, 10.831, 22.97, -2.695, 2.90, 0.40, 0.946, 0.000, -0.323),
                s.at(EleventhRib.INSTANCE, 9.817, 21.5, -1.569, 3.80, 0.40, 0.967, 0.000, -0.254));
    }
}

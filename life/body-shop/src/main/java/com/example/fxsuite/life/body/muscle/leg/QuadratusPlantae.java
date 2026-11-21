package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The quadratus plantae: under the flexor digitorum brevis, from the heel bone's underside forward to the long flexor's
 * tendon, steadying its pull.
 */
public record QuadratusPlantae() implements Muscle {

    public static final QuadratusPlantae INSTANCE = new QuadratusPlantae();

    @Override
    public String name() {
        return "quadratus plantae";
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
                s.at(Calcaneus.INSTANCE, 0, -4.4, -0.6, 2.0, 0.8, 0, -1, 0),
                s.at(Navicular.INSTANCE, -0.6, -4.3, 3.6, 1.4, 0.6, 0, -1, 0));
    }
}

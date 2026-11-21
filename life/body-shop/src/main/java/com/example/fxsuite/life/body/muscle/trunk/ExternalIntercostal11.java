package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.EleventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the eleventh space: from behind round to where the ribs become cartilage, between the
 * eleventh and the twelfth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal11() implements Muscle {

    public static final ExternalIntercostal11 INSTANCE = new ExternalIntercostal11();

    @Override
    public String name() {
        return "external intercostal, eleventh space";
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
                s.at(EleventhRib.INSTANCE, 2.003, 27.94, -6.602, 1.95, 0.45, 0.255, 0.000, -0.967),
                s.at(EleventhRib.INSTANCE, 4.871, 27.4, -7.823, 2.11, 0.45, 0.483, 0.000, -0.875),
                s.at(EleventhRib.INSTANCE, 10.721, 24.8, -4.675, 1.79, 0.45, 0.884, 0.000, -0.468),
                s.at(EleventhRib.INSTANCE, 10.794, 22.1, -2.196, 3.41, 0.45, 0.959, 0.000, -0.284));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The transversus abdominis: the belly's deepest wall, from the iliac crest, the thoracolumbar fascia and the lowest
 * ribs' cartilages, round the belly into the rectus's sheath.
 */
public record TransversusAbdominis() implements Muscle {

    public static final TransversusAbdominis INSTANCE = new TransversusAbdominis();

    @Override
    public String name() {
        return "transversus abdominis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.ABDOMEN;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(L3.INSTANCE, 11.35, 15, 1, 10.0, 0.5, 1, 0, 0),
                s.at(L3.INSTANCE, 8.85, 15, 6, 10.0, 0.5, 0.6, 0, 0.8),
                s.at(L3.INSTANCE, 4, 15, 7.8, 10.0, 0.4, 0, 0, 1));
    }
}

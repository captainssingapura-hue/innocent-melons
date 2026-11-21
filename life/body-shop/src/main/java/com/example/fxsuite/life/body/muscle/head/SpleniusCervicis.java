package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The splenius cervicis: from the spines of the third to the sixth thoracic vertebrae, up and out to the transverse
 * processes of the upper cervical vertebrae.
 */
public record SpleniusCervicis() implements Muscle {

    public static final SpleniusCervicis INSTANCE = new SpleniusCervicis();

    @Override
    public String name() {
        return "splenius cervicis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T4.INSTANCE, 1, 45, -10.7, 1.2, 0.6, 0, 0, -1),
                s.at(T1.INSTANCE, 2.314, 50.6, -7.713, 1.2, 0.6, 0.3, 0, -1),
                s.at(Atlas.INSTANCE, 3.666, 60, -1.732, 1.0, 0.6, 0.5, 0, -1));
    }
}

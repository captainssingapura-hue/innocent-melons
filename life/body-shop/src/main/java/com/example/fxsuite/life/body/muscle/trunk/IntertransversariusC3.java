package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the C3 and the C4: short, between the tips of their transverse processes.
 */
public record IntertransversariusC3() implements Muscle {

    public static final IntertransversariusC3 INSTANCE = new IntertransversariusC3();

    @Override
    public String name() {
        return "intertransversarius, C3 to C4";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(C3.INSTANCE, 2.7, 57.9, -1.98, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(C4.INSTANCE, 2.8, 56.94, -2, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

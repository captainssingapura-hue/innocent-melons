package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the C4 and the C5: short, between the tips of their transverse processes.
 */
public record IntertransversariusC4() implements Muscle {

    public static final IntertransversariusC4 INSTANCE = new IntertransversariusC4();

    @Override
    public String name() {
        return "intertransversarius, C4 to C5";
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
                s.at(C4.INSTANCE, 2.8, 56.94, -2, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(C5.INSTANCE, 3.05, 56.02, -2.28, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the C5 and the C6: short, between the tips of their transverse processes.
 */
public record IntertransversariusC5() implements Muscle {

    public static final IntertransversariusC5 INSTANCE = new IntertransversariusC5();

    @Override
    public String name() {
        return "intertransversarius, C5 to C6";
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
                s.at(C5.INSTANCE, 3.05, 56.02, -2.28, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(C6.INSTANCE, 3.3, 54.94, -2.81, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

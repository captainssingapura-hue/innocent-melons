package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the C6 and the C7: short, between the tips of their transverse processes.
 */
public record IntertransversariusC6() implements Muscle {

    public static final IntertransversariusC6 INSTANCE = new IntertransversariusC6();

    @Override
    public String name() {
        return "intertransversarius, C6 to C7";
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
                s.at(C6.INSTANCE, 3.3, 54.94, -2.81, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(C7.INSTANCE, 3.4, 53.6, -3.67, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

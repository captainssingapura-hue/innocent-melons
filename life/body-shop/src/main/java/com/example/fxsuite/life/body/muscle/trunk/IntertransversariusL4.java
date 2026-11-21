package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L4;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the L4 and the L5: short, between the tips of their transverse processes.
 */
public record IntertransversariusL4() implements Muscle {

    public static final IntertransversariusL4 INSTANCE = new IntertransversariusL4();

    @Override
    public String name() {
        return "intertransversarius, L4 to L5";
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
                s.at(L4.INSTANCE, 4.35, 16.22, -4.77, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(L5.INSTANCE, 4.2, 13.42, -5.16, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.L2;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the L1 and the L2: short, between the tips of their transverse processes.
 */
public record IntertransversariusL1() implements Muscle {

    public static final IntertransversariusL1 INSTANCE = new IntertransversariusL1();

    @Override
    public String name() {
        return "intertransversarius, L1 to L2";
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
                s.at(L1.INSTANCE, 3.6, 24.48, -5.6, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(L2.INSTANCE, 4.05, 21.79, -5.01, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L2;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the L2 and the L3: short, between the tips of their transverse processes.
 */
public record IntertransversariusL2() implements Muscle {

    public static final IntertransversariusL2 INSTANCE = new IntertransversariusL2();

    @Override
    public String name() {
        return "intertransversarius, L2 to L3";
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
                s.at(L2.INSTANCE, 4.05, 21.79, -5.01, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(L3.INSTANCE, 4.5, 19.02, -4.75, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

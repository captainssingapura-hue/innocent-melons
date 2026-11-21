package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The intertransversarius between the L3 and the L4: short, between the tips of their transverse processes.
 */
public record IntertransversariusL3() implements Muscle {

    public static final IntertransversariusL3 INSTANCE = new IntertransversariusL3();

    @Override
    public String name() {
        return "intertransversarius, L3 to L4";
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
                s.at(L3.INSTANCE, 4.5, 19.02, -4.75, 0.60, 0.50, 0.000, 0.000, -1.000),
                s.at(L4.INSTANCE, 4.35, 16.22, -4.77, 0.60, 0.50, 0.000, 0.000, -1.000));
    }
}

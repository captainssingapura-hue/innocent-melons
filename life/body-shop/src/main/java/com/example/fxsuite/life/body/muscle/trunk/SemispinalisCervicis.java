package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semispinalis cervicis: from the upper thoracic transverse processes, up to the spines of the cervical vertebrae
 * from the axis down.
 */
public record SemispinalisCervicis() implements Muscle {

    /** Its thoracic part, from the upper thoracic transverse processes, as thin as drawn: its bulk in the neck. */
    @Override
    public boolean keptAsDrawn(final int i) {
        return i == 1;
    }

    public static final SemispinalisCervicis INSTANCE = new SemispinalisCervicis();

    @Override
    public String name() {
        return "semispinalis cervicis";
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
                s.at(T5.INSTANCE, 2.6, 43, -7.3, 0.6, 0.3, 0, 0, -1),
                s.at(T2.INSTANCE, 1.8, 49.2, -6.2, 1.1, 0.6, 0, 0, -1),
                s.at(C7.INSTANCE, 1.4, 52.4, -5.6, 1.6, 1.0, 0, 0, -1),
                s.at(C5.INSTANCE, 1.05, 55.0, -5.0, 2.2, 1.3, 0, 0, -1),
                s.at(Axis.INSTANCE, 0.6, 58.4, -4.3, 1.0, 0.6, 0, 0, -1));
    }
}

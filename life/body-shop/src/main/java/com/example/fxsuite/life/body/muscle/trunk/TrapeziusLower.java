package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The trapezius's lower part: from the spines of the lower thoracic vertebrae, up and out to the root of the spine of
 * the scapula.
 */
public record TrapeziusLower() implements Muscle {

    public static final TrapeziusLower INSTANCE = new TrapeziusLower();

    @Override
    public String name() {
        return "trapezius, lower";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T12.INSTANCE, 0.8, 26, -9.95, 3.0, 0.5, 0, 0, -1),
                s.at(T8.INSTANCE, 3, 34.5, -11.6, 7.0, 0.8, 0, 0, -1),
                s.at(Scapula.INSTANCE, 6.8, 43, -10.75, 3.0, 0.5, 0, 0, -1));
    }
}

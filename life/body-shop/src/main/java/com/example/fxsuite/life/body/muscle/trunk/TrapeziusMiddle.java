package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The trapezius's middle part: from the spines of the seventh cervical and the upper thoracic vertebrae, straight out
 * across the top of the shoulder blade, to the acromion and the spine of the scapula.
 */
public record TrapeziusMiddle() implements Muscle {

    public static final TrapeziusMiddle INSTANCE = new TrapeziusMiddle();

    @Override
    public String name() {
        return "trapezius, middle";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T1.INSTANCE, 0.8, 51, -9.6, 5.0, 0.8, 0, 0, -1),
                s.at(Scapula.INSTANCE, 11, 46.502, -9.51, 5.0, 1.0, 0, 0.2, -1),
                s.at(Scapula.INSTANCE, 15, 47.532, -6.466, 3.0, 0.6, 0, 1, -0.5));
    }
}

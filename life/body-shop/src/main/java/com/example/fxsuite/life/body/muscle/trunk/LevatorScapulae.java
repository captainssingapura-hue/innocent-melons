package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The levator scapulae: from the transverse processes of the upper four cervical vertebrae, down the side of the neck,
 * to the scapula's upper angle.
 */
public record LevatorScapulae() implements Muscle {

    public static final LevatorScapulae INSTANCE = new LevatorScapulae();

    @Override
    public String name() {
        return "levator scapulae";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Atlas.INSTANCE, 3.521, 60.1, -1.656, 1.2, 1.0, 1, 0, -0.3),
                s.at(C4.INSTANCE, 3.974, 56, -3.287, 1.8, 1.4, 1, 0, -0.5),
                s.at(Scapula.INSTANCE, 7.4, 47.421, -9.042, 1.6, 0.8, 0, 0.5, -1));
    }
}

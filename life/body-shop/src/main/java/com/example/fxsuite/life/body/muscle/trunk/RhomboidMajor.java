package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rhomboid major: from the spines of the second to the fifth thoracic vertebrae, down and out to the scapula's
 * inner border below its spine.
 */
public record RhomboidMajor() implements Muscle {

    public static final RhomboidMajor INSTANCE = new RhomboidMajor();

    @Override
    public String name() {
        return "rhomboid major";
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
                s.at(T4.INSTANCE, 0.8, 45, -11.4, 6.0, 0.8, 0, 0, -1),
                s.at(Scapula.INSTANCE, 8.059, 39, -9.706, 7.0, 0.6, -0.2, 0, -1));
    }
}

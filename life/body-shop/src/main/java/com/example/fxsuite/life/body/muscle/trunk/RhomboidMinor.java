package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rhomboid minor: from the spines of the seventh cervical and the first thoracic vertebrae, down and out to the
 * scapula's inner border at the root of its spine.
 */
public record RhomboidMinor() implements Muscle {

    public static final RhomboidMinor INSTANCE = new RhomboidMinor();

    @Override
    public String name() {
        return "rhomboid minor";
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
                s.at(C7.INSTANCE, 0.8, 52, -8.5, 2.0, 0.6, 0, 0, -1),
                s.at(Scapula.INSTANCE, 7.049, 44.6, -9.955, 2.0, 0.5, -0.2, 0, -1));
    }
}

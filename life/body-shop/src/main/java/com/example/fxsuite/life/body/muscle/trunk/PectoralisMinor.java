package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pectoralis minor: from the third, fourth and fifth ribs in front, up under the pectoralis major to the coracoid.
 */
public record PectoralisMinor() implements Muscle {

    public static final PectoralisMinor INSTANCE = new PectoralisMinor();

    @Override
    public String name() {
        return "pectoralis minor";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 9.422, 35.5, 8.408, 4.0, 0.8, 0.2, 0, 1),
                s.at(Scapula.INSTANCE, 14.4, 44.4, 3.3, 1.2, 0.6, 0, 0, 1));
    }
}

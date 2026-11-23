package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pectoralis major's clavicular head: from the inner half of the clavicle, down and out across the top of the
 * chest, to the outer lip of the humerus's groove.
 */
public record PectoralisMajorClavicular() implements Muscle {

    public static final PectoralisMajorClavicular INSTANCE = new PectoralisMajorClavicular();

    @Override
    public String name() {
        return "pectoralis major, clavicular head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Clavicle.INSTANCE, 6, 45.714, 7.736, 6.0, 0.4, 0, 0.4, 1),
                s.at(Sternum.INSTANCE, 12.174, 43.049, 7.047, 5.0, 1.6, 0.3, 0.2, 1),
                s.at(Humerus.INSTANCE, 1.666, -5, 2.132, 2.4, 0.6, 0.5, 0, 1));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pectoralis major's sternal head: from the front of the sternum and the upper ribs' cartilages, the chest's broad
 * fan, gathering out to the armpit's front fold, to the outer lip of the humerus's groove.
 */
public record PectoralisMajorSternal() implements Muscle {

    public static final PectoralisMajorSternal INSTANCE = new PectoralisMajorSternal();

    @Override
    public String name() {
        return "pectoralis major, sternal head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 2.2, 38.5, 9.6, 10.0, 1.2, 0, 0, 1),
                s.at(Sternum.INSTANCE, 7.891, 38.5, 9.305, 10.0, 2.2, 0.1, 0, 1),
                s.at(Sternum.INSTANCE, 13.514, 40, 6.626, 6.0, 2.0, 0.5, 0, 0.9),
                s.at(Humerus.INSTANCE, 1.466, -6, 1.732, 2.6, 0.6, 0.5, 0, 1));
    }
}

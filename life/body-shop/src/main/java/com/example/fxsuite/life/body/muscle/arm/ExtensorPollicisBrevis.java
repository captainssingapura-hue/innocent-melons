package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor pollicis brevis: beside the abductor, low on the back of the radius, its tendon to the thumb's first
 * phalanx.
 */
public record ExtensorPollicisBrevis() implements Muscle {

    public static final ExtensorPollicisBrevis INSTANCE = new ExtensorPollicisBrevis();

    @Override
    public String name() {
        return "extensor pollicis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Radius.INSTANCE, 1.466, -14, -0.532, 0.9, 0.6, 0.5, 0, -1),
                s.at(Radius.INSTANCE, 2.6, -21, 0.2, 0.6, 0.4, 1, 0, 0),
                s.at(FirstMetacarpal.INSTANCE, 3.25, -3.4, 1.4, 0.5, 0.3, 1, 0, 0));
    }
}

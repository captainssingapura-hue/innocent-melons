package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor carpi radialis brevis: from the humerus's outer epicondyle, down the back of the forearm's outer side,
 * to the back of the wrist.
 */
public record ExtensorCarpiRadialisBrevis() implements Muscle {

    public static final ExtensorCarpiRadialisBrevis INSTANCE = new ExtensorCarpiRadialisBrevis();

    @Override
    public String name() {
        return "extensor carpi radialis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.5, -29.4, -0.35, 0.8, 0.5, 0.8, 0, -0.6),
                s.at(Radius.INSTANCE, 1.663, -8, -0.771, 2.2, 1.7, 0.6, 0, -1),
                s.at(Radius.INSTANCE, 1.689, -18, -0.921, 0.8, 0.6, 0.4, 0, -1),
                s.at(Radius.INSTANCE, 1.543, -23.4, -0.808, 0.5, 0.4, 0.3, 0, -1));
    }
}

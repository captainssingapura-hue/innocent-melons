package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor carpi radialis longus: from the humerus's outer ridge below the brachioradialis, down the forearm's
 * outer side behind it, to the back of the wrist.
 */
public record ExtensorCarpiRadialisLongus() implements Muscle {

    public static final ExtensorCarpiRadialisLongus INSTANCE = new ExtensorCarpiRadialisLongus();

    @Override
    public String name() {
        return "extensor carpi radialis longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.4, -26.4, -0.2, 0.9, 0.6, 1, 0, 0),
                s.at(Radius.INSTANCE, 1.911, -5, -0.384, 2.2, 1.7, 1, 0, -0.4),
                s.at(Radius.INSTANCE, 2.175, -15, -0.47, 1.0, 0.7, 1, 0, -0.4),
                s.at(Radius.INSTANCE, 2.08, -23.4, -0.64, 0.5, 0.4, 0.6, 0, -0.8));
    }
}

package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pronator teres: from the humerus's inner epicondyle, across the front of the forearm, down and out to the middle
 * of the radius; it turns the palm down.
 */
public record PronatorTeres() implements Muscle {

    public static final PronatorTeres INSTANCE = new PronatorTeres();

    @Override
    public String name() {
        return "pronator teres";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -2.776, -29, -0.047, 1.4, 1.0, -0.5, 0, 1),
                s.at(Ulna.INSTANCE, -0.6, -3.5, 1.35, 2.2, 1.7, 0, 0, 1),
                s.at(Radius.INSTANCE, 1.866, -10, 1.032, 1.0, 0.6, 0.5, 0, 1));
    }
}

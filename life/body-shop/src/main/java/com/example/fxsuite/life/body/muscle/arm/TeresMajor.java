package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The teres major: from the scapula's lower angle, out under the arm, round the humerus's inner side to the lip of its
 * groove in front; the back of the armpit.
 */
public record TeresMajor() implements Muscle {

    public static final TeresMajor INSTANCE = new TeresMajor();

    @Override
    public String name() {
        return "teres major";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 9.8, 35, -8.6, 2.6, 1.6, 0, 0, -1),
                s.at(Scapula.INSTANCE, 12.513, 37, -5.442, 2.8, 2.0, 0.3, 0, -1),
                s.at(Humerus.INSTANCE, -0.121, -4.6, -1.056, 2.0, 1.0, -1, 0, -0.3),
                s.at(Humerus.INSTANCE, 0.239, -5.4, 0.928, 1.6, 0.5, -1, 0, 0.3));
    }
}

package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The teres minor: from the scapula's outer border, under the infraspinatus, to the lowest facet of the humerus's
 * greater tubercle.
 */
public record TeresMinor() implements Muscle {

    public static final TeresMinor INSTANCE = new TeresMinor();

    @Override
    public String name() {
        return "teres minor";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 11.399, 38.6, -6.33, 1.6, 1.4, 0.3, 0, -1),
                s.at(Scapula.INSTANCE, 13.399, 40.6, -4.33, 1.6, 1.4, 0.3, 0, -1),
                s.at(Humerus.INSTANCE, 1.114, -1.8, -2.113, 1.0, 0.6, 0.3, 0, -1));
    }
}

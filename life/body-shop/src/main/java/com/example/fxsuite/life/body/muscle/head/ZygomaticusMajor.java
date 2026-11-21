package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The zygomaticus major: from the cheekbone, down and in to the corner of the mouth; it draws the smile.
 */
public record ZygomaticusMajor() implements Muscle {

    public static final ZygomaticusMajor INSTANCE = new ZygomaticusMajor();

    @Override
    public String name() {
        return "zygomaticus major";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Zygomatic.INSTANCE, 5.088, 2.4, 7.176, 1.0, 0.5, 0.5, 0, 1),
                s.at(Maxilla.INSTANCE, 3.728, 0, 8.561, 0.8, 0.5, 0.3, 0, 1),
                s.at(Maxilla.INSTANCE, 2.6, -1.6, 9.4, 0.6, 0.4, 0, 0, 1));
    }
}

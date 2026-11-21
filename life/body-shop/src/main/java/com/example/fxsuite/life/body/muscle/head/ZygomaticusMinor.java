package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The zygomaticus minor: from the cheekbone, down and in, beside the levator, into the upper lip.
 */
public record ZygomaticusMinor() implements Muscle {

    public static final ZygomaticusMinor INSTANCE = new ZygomaticusMinor();

    @Override
    public String name() {
        return "zygomaticus minor";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Zygomatic.INSTANCE, 4.357, 3, 8.456, 0.7, 0.3, 0.3, 0, 1),
                s.at(Maxilla.INSTANCE, 2.8, 0.2, 10.25, 0.7, 0.3, 0, 0, 1));
    }
}

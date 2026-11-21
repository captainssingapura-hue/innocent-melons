package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The depressor anguli oris: from the mandible's outer face low down, up to the corner of the mouth; it draws it down.
 */
public record DepressorAnguliOris() implements Muscle {

    public static final DepressorAnguliOris INSTANCE = new DepressorAnguliOris();

    @Override
    public String name() {
        return "depressor anguli oris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 3.345, -5.145, 7.216, 1.6, 0.4, 0.3, -0.3, 1),
                s.at(Mandible.INSTANCE, 2.7, -1.8, 9.8, 0.6, 0.4, 0, 0, 1));
    }
}

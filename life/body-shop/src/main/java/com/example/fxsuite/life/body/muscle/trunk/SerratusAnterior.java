package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The serratus anterior: from the outer faces of the upper eight ribs, round the side of the chest, under the shoulder
 * blade to its inner border; its fingers show below the armpit.
 */
public record SerratusAnterior() implements Muscle {

    public static final SerratusAnterior INSTANCE = new SerratusAnterior();

    @Override
    public String name() {
        return "serratus anterior";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 12.28, 32, 5.36, 12.0, 0.8, 0.8, 0, 0.6),
                s.at(Sternum.INSTANCE, 14, 34, 0, 12.0, 1.2, 1, 0, 0),
                s.at(Sternum.INSTANCE, 11.76, 38, -5.68, 10.0, 0.8, 0.6, 0, -0.8),
                s.at(Scapula.INSTANCE, 7.943, 40.5, -8.208, 10.0, 0.4, 0.3, 0, -1));
    }
}

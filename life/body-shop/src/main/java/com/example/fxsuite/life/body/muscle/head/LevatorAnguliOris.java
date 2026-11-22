package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The levator anguli oris: from the canine fossa of the maxilla, under the levator of the lip, down to the corner of
 * the mouth.
 */
public record LevatorAnguliOris() implements Muscle {

    public static final LevatorAnguliOris INSTANCE = new LevatorAnguliOris();

    @Override
    public String name() {
        return "levator anguli oris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 2.6, 2, 9, 1.0, 0.4, 0, 0, 1),
                s.at(Skull.INSTANCE, 2.6, -1.2, 10, 1.0, 0.4, 0, 0, 1));
    }
}

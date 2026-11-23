package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus capitis posterior major: from the axis's spinous process, up and out to the occiput below its inferior
 * nuchal line.
 */
public record RectusCapitisPosteriorMajor() implements Muscle {

    public static final RectusCapitisPosteriorMajor INSTANCE = new RectusCapitisPosteriorMajor();

    @Override
    public String name() {
        return "rectus capitis posterior major";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Axis.INSTANCE, 0.4, 58.5, -4.7, 1.2, 0.6, 0, 0, -1),
                s.at(Skull.INSTANCE, 2, 1.8, -6.1, 1.2, 0.6, 0, 0, -1));
    }
}

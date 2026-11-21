package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus capitis posterior minor: from the atlas's posterior tubercle up to the occiput beside the midline.
 */
public record RectusCapitisPosteriorMinor() implements Muscle {

    public static final RectusCapitisPosteriorMinor INSTANCE = new RectusCapitisPosteriorMinor();

    @Override
    public String name() {
        return "rectus capitis posterior minor";
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
                s.at(Atlas.INSTANCE, 0.3, 60.4, -3.65, 0.9, 0.5, 0, 0, -1),
                s.at(Occipital.INSTANCE, 0.9, 2, -6.35, 0.9, 0.5, 0, 0, -1));
    }
}

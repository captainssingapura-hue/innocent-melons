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
 * The rectus capitis lateralis: from the atlas's transverse process up to the occiput's jugular process.
 */
public record RectusCapitisLateralis() implements Muscle {

    public static final RectusCapitisLateralis INSTANCE = new RectusCapitisLateralis();

    @Override
    public String name() {
        return "rectus capitis lateralis";
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
                s.at(Atlas.INSTANCE, 3.55, 60.3, -1.2, 0.7, 0.5, 1, 0, 0),
                s.at(Occipital.INSTANCE, 3.15, -0.2, -1, 0.7, 0.5, 1, 0, 0));
    }
}

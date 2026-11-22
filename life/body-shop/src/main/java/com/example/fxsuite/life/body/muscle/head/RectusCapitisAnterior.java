package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus capitis anterior: a short muscle from the atlas's lateral mass up to the base of the occiput before its
 * condyle.
 */
public record RectusCapitisAnterior() implements Muscle {

    public static final RectusCapitisAnterior INSTANCE = new RectusCapitisAnterior();

    @Override
    public String name() {
        return "rectus capitis anterior";
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
                s.at(Atlas.INSTANCE, 1.6, 60.2, 0.15, 0.8, 0.5, 0, 0, 1),
                s.at(Skull.INSTANCE, 1.2, -0.2, 0.75, 0.8, 0.5, 0, 0, 1));
    }
}

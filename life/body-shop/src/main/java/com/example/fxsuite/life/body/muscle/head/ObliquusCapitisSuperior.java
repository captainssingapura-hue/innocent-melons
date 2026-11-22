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
 * The obliquus capitis superior: from the atlas's transverse process, up and back to the occiput between the nuchal
 * lines.
 */
public record ObliquusCapitisSuperior() implements Muscle {

    public static final ObliquusCapitisSuperior INSTANCE = new ObliquusCapitisSuperior();

    @Override
    public String name() {
        return "obliquus capitis superior";
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
                s.at(Atlas.INSTANCE, 3.728, 60.4, -1.561, 0.8, 0.5, 0.3, 0, -1),
                s.at(Skull.INSTANCE, 2.928, 2.2, -5.361, 0.8, 0.5, 0.3, 0, -1));
    }
}

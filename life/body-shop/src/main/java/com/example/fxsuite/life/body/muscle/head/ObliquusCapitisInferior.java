package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The obliquus capitis inferior: from the axis's spinous process, out to the atlas's transverse process; it turns the
 * head.
 */
public record ObliquusCapitisInferior() implements Muscle {

    public static final ObliquusCapitisInferior INSTANCE = new ObliquusCapitisInferior();

    @Override
    public String name() {
        return "obliquus capitis inferior";
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
                s.at(Axis.INSTANCE, 0.6, 58.6, -4.45, 1.0, 0.7, 0, 0, -1),
                s.at(Atlas.INSTANCE, 3.699, 60.2, -1.465, 1.0, 0.7, 0.3, 0, -1));
    }
}

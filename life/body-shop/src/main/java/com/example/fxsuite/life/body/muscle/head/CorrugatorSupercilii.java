package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The corrugator supercilii: from the inner end of the brow ridge, out along the brow under the frontalis into its
 * skin; it draws the brows together.
 */
public record CorrugatorSupercilii() implements Muscle {

    public static final CorrugatorSupercilii INSTANCE = new CorrugatorSupercilii();

    @Override
    public String name() {
        return "corrugator supercilii";
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
                s.at(Skull.INSTANCE, 0.9, 7.361, 9.404, 0.6, 0.4, 0, 0.2, 1),
                s.at(Skull.INSTANCE, 2.6, 7.961, 9.404, 0.6, 0.4, 0, 0.2, 1));
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The subclavius: from the first rib by its cartilage, out under the clavicle to its middle third; it steadies the
 * clavicle.
 */
public record Subclavius() implements Muscle {

    public static final Subclavius INSTANCE = new Subclavius();

    @Override
    public String name() {
        return "subclavius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(FirstRib.INSTANCE, 2.8, 44.472, 5.761, 0.6, 0.5, 0, -0.3, 1),
                s.at(Clavicle.INSTANCE, 6, 45.935, 6.699, 1.0, 0.7, 0, -1, 0.3),
                s.at(Clavicle.INSTANCE, 10, 46.039, 5.528, 0.8, 0.5, 0, -1, 0.3));
    }
}

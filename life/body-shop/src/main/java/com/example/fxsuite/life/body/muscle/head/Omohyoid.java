package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The omohyoid: from the scapula's upper border by its notch, forward across the lower neck under the
 * sternocleidomastoid to its intermediate tendon, then up beside the sternohyoid to the hyoid.
 */
public record Omohyoid() implements Muscle {

    public static final Omohyoid INSTANCE = new Omohyoid();

    @Override
    public String name() {
        return "omohyoid";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 11.6, 46.8, -5.2, 0.8, 0.4, 0, 1, 0),
                s.at(C6.INSTANCE, 3.943, 51.5, 1.808, 0.5, 0.4, 0.3, 0, 1),
                s.at(C4.INSTANCE, 2.6, 55, 3.2, 0.9, 0.4, 0, 0, 1),
                s.at(Hyoid.INSTANCE, 1.8, 4.6, 4.25, 0.7, 0.3, 0, 0, 1));
    }
}

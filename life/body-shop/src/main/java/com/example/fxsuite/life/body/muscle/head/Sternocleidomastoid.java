package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The sternocleidomastoid: from the top of the sternum and the inner end of the clavicle, up and back across the side
 * of the neck, to the mastoid behind the ear.
 */
public record Sternocleidomastoid() implements Muscle {

    public static final Sternocleidomastoid INSTANCE = new Sternocleidomastoid();

    @Override
    public String name() {
        return "sternocleidomastoid";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 1.6, 45.702, 7.11, 1.6, 1.0, 0, 0.2, 1),
                s.at(C6.INSTANCE, 3.116, 52, 3.56, 3.4, 1.4, 0.4, 0, 0.9),
                s.at(C3.INSTANCE, 4.006, 57, 0.629, 3.0, 1.4, 0.8, 0, 0.5),
                s.at(Temporal.INSTANCE, 4.31, -0.6, -0.902, 2.0, 1.0, 1, 0, -0.2));
    }
}

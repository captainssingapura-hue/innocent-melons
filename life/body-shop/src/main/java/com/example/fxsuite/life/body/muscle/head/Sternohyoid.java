package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The sternohyoid: a thin strap from the back of the top of the sternum, up the front of the neck beside the midline,
 * to the hyoid.
 */
public record Sternohyoid() implements Muscle {

    public static final Sternohyoid INSTANCE = new Sternohyoid();

    @Override
    public String name() {
        return "sternohyoid";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 1, 46, 6.6, 1.2, 0.4, 0, 0, 1),
                s.at(C6.INSTANCE, 1.2, 52, 4.6, 1.4, 0.4, 0, 0, 1),
                s.at(Hyoid.INSTANCE, 1, 4.4, 4.65, 1.0, 0.3, 0, 0, 1));
    }
}

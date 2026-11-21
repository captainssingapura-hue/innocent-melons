package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The supinator: from the humerus's outer epicondyle and the ulna's crest, wrapped round the radius's neck; it turns
 * the palm up.
 */
public record Supinator() implements Muscle {

    public static final Supinator INSTANCE = new Supinator();

    @Override
    public String name() {
        return "supinator";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Ulna.INSTANCE, 0.514, -3.6, -1.113, 1.6, 0.6, 0.3, 0, -1),
                s.at(Radius.INSTANCE, 1.9, -6, 0.4, 1.8, 0.6, 1, 0, 0));
    }
}

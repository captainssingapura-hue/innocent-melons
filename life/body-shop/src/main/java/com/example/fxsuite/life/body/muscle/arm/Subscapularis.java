package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The subscapularis: on the front of the scapula, against the ribs, out to the humerus's lesser tubercle.
 */
public record Subscapularis() implements Muscle {

    public static final Subscapularis INSTANCE = new Subscapularis();

    @Override
    public String name() {
        return "subscapularis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 9.502, 40, -8.69, 6.0, 1.0, 0.2, 0, 1),
                s.at(Scapula.INSTANCE, 13.6, 42.4, -3.7, 3.6, 1.4, 0, 0, 1),
                s.at(Humerus.INSTANCE, 0.4, -0.6, 1.75, 1.6, 0.5, 0, 0, 1));
    }
}

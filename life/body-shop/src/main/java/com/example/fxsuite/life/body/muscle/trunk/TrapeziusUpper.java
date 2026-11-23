package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The trapezius's upper part: from the occiput and the ligament down the back of the neck, down and out over the slope
 * of the shoulder, to the outer third of the clavicle.
 */
public record TrapeziusUpper() implements Muscle {

    public static final TrapeziusUpper INSTANCE = new TrapeziusUpper();

    @Override
    public String name() {
        return "trapezius, upper";
    }

    @Override
    public boolean sheet() {
        return true;
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 2.37, 4.4, -8.501, 3.0, 0.6, 0.1, 0, -1),
                s.at(C4.INSTANCE, 2.863, 56.6, -5.714, 3.0, 1.4, 0.2, 0, -1),
                s.at(C7.INSTANCE, 8.267, 50.723, -3.811, 4.5, 1.8, 0.3, 1, -0.5),
                s.at(Clavicle.INSTANCE, 14.423, 47.415, 1.677, 3.5, 0.4, 0.2, 1, -0.2));
    }
}

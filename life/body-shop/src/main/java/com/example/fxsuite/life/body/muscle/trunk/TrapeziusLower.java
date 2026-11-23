package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.muscle.Anchor;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleForm;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;

import java.util.List;

/**
 * The trapezius's lower part, a fan: from the spines of the fourth to the twelfth thoracic vertebrae, along the middle
 * of the back, its fibres gathering up and out to the tubercle at the root of the spine of the scapula, the highest
 * running nearly level, the lowest steeply up. A sheet spread from the one line to the other ({@link MuscleForm}),
 * lying just behind the spines' tips; its upper edge the middle part's lower ({@link TrapeziusMiddle}), so that the
 * two meet.
 */
public record TrapeziusLower() implements Muscle {

    public static final TrapeziusLower INSTANCE = new TrapeziusLower();

    @Override
    public String name() {
        return "trapezius, lower";
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
    public MuscleForm.Spread form() {
        return new MuscleForm.Spread(
                List.of(new Anchor(T4.INSTANCE, spines()),
                        new Anchor(T8.INSTANCE, new V3(0.5, 33.2, -10.8)),
                        new Anchor(T12.INSTANCE, new V3(0.5, 24.9, -8.9))),
                List.of(new Anchor(Scapula.INSTANCE, root()),
                        new Anchor(Scapula.INSTANCE, new V3(7.2, 41.9, -10.9))),
                0.6, new V3(0, 0, -1));
    }

    /** Where its line along the spines begins, the middle part's ends: between the third and fourth spines' tips. */
    public V3 spines() {
        return new V3(0.5, 45.4, -10.85);
    }

    /** Where its line on the scapula begins, the middle part's ends: at the top of the tubercle at the spine's root. */
    public V3 root() {
        return new V3(6.5, 44.4, -10.65);
    }

    @Override
    public List<Section> sections() {
        return form().sections();
    }
}

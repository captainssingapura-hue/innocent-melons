package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.muscle.Anchor;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleForm;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;

import java.util.List;

/**
 * The trapezius's middle part: from the spines of the seventh cervical to the third thoracic vertebra, out across the
 * top of the shoulder blade, the highest fibres to the acromion, the lowest to the spine of the scapula near its root.
 * A sheet spread from the one line to the other ({@link MuscleForm}), lying just behind the spines' tips; its lower
 * edge the lower part's upper ({@link TrapeziusLower}), between the third and fourth spines and at the spine's root,
 * so that the two meet.
 */
public record TrapeziusMiddle() implements Muscle {

    public static final TrapeziusMiddle INSTANCE = new TrapeziusMiddle();

    @Override
    public String name() {
        return "trapezius, middle";
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
                List.of(new Anchor(C7.INSTANCE, new V3(0.5, 54.35, -7.9)),
                        new Anchor(T1.INSTANCE, new V3(0.5, 51.8, -8.9)),
                        new Anchor(T3.INSTANCE, TrapeziusLower.INSTANCE.spines())),
                List.of(new Anchor(Scapula.INSTANCE, new V3(15, 47.532, -6.466)),
                        new Anchor(Scapula.INSTANCE, TrapeziusLower.INSTANCE.root())),
                0.9, new V3(0, 0, -1));
    }

    @Override
    public List<Section> sections() {
        return form().sections();
    }
}

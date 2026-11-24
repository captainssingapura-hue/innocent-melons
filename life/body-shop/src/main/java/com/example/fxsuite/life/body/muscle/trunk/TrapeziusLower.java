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

    /** How far it reaches in under the middle part along their seam, cm. */
    private static final double OVERLAP = 0.8;

    @Override
    public MuscleForm.Spread form() {
        return new MuscleForm.Spread(
                // begun a little up under the middle part, so the two overlap along their seam, not just meet
                List.of(new Anchor(T4.INSTANCE, spines().plus(new V3(0, OVERLAP, 0))),
                        new Anchor(T4.INSTANCE, spines()),
                        new Anchor(T8.INSTANCE, new V3(0.1, 33.2, -11.9)),
                        new Anchor(T12.INSTANCE, new V3(0.1, 24.9, -10.0))),
                List.of(new Anchor(Scapula.INSTANCE, underMiddle()),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("crest's root")),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("root of the spine"))),
                0.6, new V3(0, 0, -1));
    }

    /**
     * Where its line on the scapula begins, a little out along the crest from its root, under the middle part's end:
     * two parts meeting edge to edge leave a hairline between them where each thins to its edge.
     */
    private V3 underMiddle() {
        final V3 root = Scapula.INSTANCE.at("crest's root");
        return root.plus(Scapula.INSTANCE.at("crest").minus(root).unit().times(OVERLAP));
    }

    /**
     * Where its line along the spines begins, the middle part's ends: between the third and fourth spines' tips, half
     * a centimetre behind them, as the line all down them lies.
     */
    public V3 spines() {
        return new V3(0.1, 45.4, -11.95);
    }

    @Override
    public List<Section> sections() {
        return form().sections();
    }
}

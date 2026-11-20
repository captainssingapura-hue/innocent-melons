package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The spine's twenty-six bones: seven cervical vertebrae, twelve thoracic, five lumbar, the sacrum, the coccyx. */
public record SpineBones() implements StatelessFunctionalObject {

    public static final SpineBones INSTANCE = new SpineBones();

    public List<Bone> bones() {
        return List.of(Atlas.INSTANCE, Axis.INSTANCE, C3.INSTANCE, C4.INSTANCE, C5.INSTANCE, C6.INSTANCE,
                C7.INSTANCE, T1.INSTANCE, T2.INSTANCE, T3.INSTANCE, T4.INSTANCE, T5.INSTANCE, T6.INSTANCE,
                T7.INSTANCE, T8.INSTANCE, T9.INSTANCE, T10.INSTANCE, T11.INSTANCE, T12.INSTANCE, L1.INSTANCE,
                L2.INSTANCE, L3.INSTANCE, L4.INSTANCE, L5.INSTANCE, Sacrum.INSTANCE, Coccyx.INSTANCE);
    }
}

package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The skull's joints: where it nods on the atlas; the jaw; the teeth in their sockets; the middle ear's. Its sutures
 * none of them, its bones knit into one.
 */
public record SkullJoints() implements StatelessFunctionalObject {

    public static final SkullJoints INSTANCE = new SkullJoints();

    public List<Complex> joints() {
        return List.of(AtlantoOccipital.INSTANCE, Temporomandibular.INSTANCE, UpperGomphoses.INSTANCE,
                LowerGomphoses.INSTANCE, Stapediovestibular.INSTANCE, Incudostapedial.INSTANCE,
                Incudomalleolar.INSTANCE);
    }
}

package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The skull's joints: where it nods on the atlas; its sutures; the jaw; the teeth in their sockets; the middle ear's.
 */
public record SkullJoints() implements StatelessFunctionalObject {

    public static final SkullJoints INSTANCE = new SkullJoints();

    public List<Complex> joints() {
        return List.of(AtlantoOccipital.INSTANCE, CoronalSuture.INSTANCE, SagittalSuture.INSTANCE,
                LambdoidSuture.INSTANCE, SquamousSuture.INSTANCE, OccipitomastoidSuture.INSTANCE,
                SphenoOccipital.INSTANCE, SphenofrontalSuture.INSTANCE, SphenosquamosalSuture.INSTANCE,
                SphenoparietalSuture.INSTANCE, FrontoethmoidalSuture.INSTANCE, SphenoethmoidalSuture.INSTANCE,
                FrontonasalSuture.INSTANCE, InternasalSuture.INSTANCE, FrontomaxillarySuture.INSTANCE,
                NasomaxillarySuture.INSTANCE, IntermaxillarySuture.INSTANCE, FrontozygomaticSuture.INSTANCE,
                ZygomaticomaxillarySuture.INSTANCE, TemporozygomaticSuture.INSTANCE, FrontolacrimalSuture.INSTANCE,
                LacrimomaxillarySuture.INSTANCE, EthmoidolacrimalSuture.INSTANCE, PalatomaxillarySuture.INSTANCE,
                MedianPalatineSuture.INSTANCE, SphenopalatineSuture.INSTANCE, ConchomaxillarySuture.INSTANCE,
                SphenovomerineSuture.INSTANCE, VomeroethmoidalSuture.INSTANCE, VomeromaxillarySuture.INSTANCE,
                UpperGomphoses.INSTANCE, LowerGomphoses.INSTANCE, Stapediovestibular.INSTANCE,
                Incudostapedial.INSTANCE, Incudomalleolar.INSTANCE, Temporomandibular.INSTANCE);
    }
}

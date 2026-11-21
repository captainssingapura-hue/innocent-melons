package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The chest's joints: each rib on its vertebra, on its cartilage; the cartilages on the sternum. */
public record ThoraxJoints() implements StatelessFunctionalObject {

    public static final ThoraxJoints INSTANCE = new ThoraxJoints();

    public List<Complex> joints() {
        return List.of(FirstCostovertebral.INSTANCE, SecondCostovertebral.INSTANCE, ThirdCostovertebral.INSTANCE,
                FourthCostovertebral.INSTANCE, FifthCostovertebral.INSTANCE, SixthCostovertebral.INSTANCE,
                SeventhCostovertebral.INSTANCE, EighthCostovertebral.INSTANCE, NinthCostovertebral.INSTANCE,
                TenthCostovertebral.INSTANCE, EleventhCostovertebral.INSTANCE, TwelfthCostovertebral.INSTANCE,
                Costochondral.INSTANCE, Sternocostal.INSTANCE);
    }
}

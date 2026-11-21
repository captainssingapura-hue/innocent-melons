package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The foot's twenty-six bones a side: its tarsals, its metatarsals and its toes' phalanges. */
public record FootBones() implements StatelessFunctionalObject {

    public static final FootBones INSTANCE = new FootBones();

    public List<Bone> bones() {
        return List.of(Talus.INSTANCE, Calcaneus.INSTANCE, Navicular.INSTANCE, Cuboid.INSTANCE,
                MedialCuneiform.INSTANCE, IntermediateCuneiform.INSTANCE, LateralCuneiform.INSTANCE,
                FirstMetatarsal.INSTANCE, SecondMetatarsal.INSTANCE, ThirdMetatarsal.INSTANCE,
                FourthMetatarsal.INSTANCE, FifthMetatarsal.INSTANCE, GreatToeProximalPhalanx.INSTANCE,
                GreatToeDistalPhalanx.INSTANCE, SecondToeProximalPhalanx.INSTANCE, SecondToeMiddlePhalanx.INSTANCE,
                SecondToeDistalPhalanx.INSTANCE, ThirdToeProximalPhalanx.INSTANCE, ThirdToeMiddlePhalanx.INSTANCE,
                ThirdToeDistalPhalanx.INSTANCE, FourthToeProximalPhalanx.INSTANCE, FourthToeMiddlePhalanx.INSTANCE,
                FourthToeDistalPhalanx.INSTANCE, LittleToeProximalPhalanx.INSTANCE, LittleToeMiddlePhalanx.INSTANCE,
                LittleToeDistalPhalanx.INSTANCE);
    }
}

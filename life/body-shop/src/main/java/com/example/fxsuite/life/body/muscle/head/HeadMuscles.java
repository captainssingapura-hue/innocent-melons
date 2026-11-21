package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The neck's and the head's muscles: the platysma, the sternocleidomastoid, the three scalenes, the muscles above and
 * below the hyoid, the deep ones before the spine, the suboccipitals, the splenius, the semispinalis and the
 * longissimus of the head; the jaw's, the masseter, the temporalis and the pterygoids; the face's, round the eyes, the
 * nose and the mouth, and the ear's.
 */
public record HeadMuscles() implements StatelessFunctionalObject {

    public static final HeadMuscles INSTANCE = new HeadMuscles();

    public List<Muscle> muscles() {
        return List.of(Sternocleidomastoid.INSTANCE, SpleniusCapitis.INSTANCE, Sternohyoid.INSTANCE,
                Mylohyoid.INSTANCE, Masseter.INSTANCE, Temporalis.INSTANCE, Frontalis.INSTANCE,
                ZygomaticusMajor.INSTANCE, Occipitalis.INSTANCE, Procerus.INSTANCE, CorrugatorSupercilii.INSTANCE,
                OrbicularisOculi.INSTANCE, Nasalis.INSTANCE, LevatorLabiiSuperiorisAlaequeNasi.INSTANCE,
                LevatorLabiiSuperioris.INSTANCE, ZygomaticusMinor.INSTANCE, Risorius.INSTANCE,
                LevatorAnguliOris.INSTANCE, DepressorAnguliOris.INSTANCE, DepressorLabiiInferioris.INSTANCE,
                Mentalis.INSTANCE, OrbicularisOris.INSTANCE, Buccinator.INSTANCE, AuricularisSuperior.INSTANCE,
                AuricularisAnterior.INSTANCE, AuricularisPosterior.INSTANCE, LateralPterygoid.INSTANCE,
                MedialPterygoid.INSTANCE, Platysma.INSTANCE, ScaleneAnterior.INSTANCE, ScaleneMiddle.INSTANCE,
                ScalenePosterior.INSTANCE, DigastricAnterior.INSTANCE, DigastricPosterior.INSTANCE,
                Stylohyoid.INSTANCE, Geniohyoid.INSTANCE, Sternothyroid.INSTANCE, Thyrohyoid.INSTANCE,
                Omohyoid.INSTANCE, LongusColli.INSTANCE, LongusCapitis.INSTANCE, RectusCapitisAnterior.INSTANCE,
                RectusCapitisLateralis.INSTANCE, RectusCapitisPosteriorMajor.INSTANCE,
                RectusCapitisPosteriorMinor.INSTANCE, ObliquusCapitisInferior.INSTANCE,
                ObliquusCapitisSuperior.INSTANCE, SemispinalisCapitis.INSTANCE, SpleniusCervicis.INSTANCE,
                LongissimusCapitis.INSTANCE);
    }
}

package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The trunk's muscles: the chest's, the pectorals, the serratus, the subclavius, the intercostals space by space, the
 * diaphragm; the belly's, the rectus, the obliques, the transversus, the pyramidalis; the back's, the trapezius's three
 * parts, the latissimus, the rhomboids, the levator scapulae, the serratus posterior, the erector spinae's three
 * columns, the semispinalis, the multifidus, and the short ones level by level, the rotatores, the interspinales, the
 * intertransversarii, the levatores costarum; the quadratus lumborum; the pelvic floor's.
 */
public record TrunkMuscles() implements StatelessFunctionalObject {

    public static final TrunkMuscles INSTANCE = new TrunkMuscles();

    public List<Muscle> muscles() {
        return List.of(PectoralisMajorClavicular.INSTANCE, PectoralisMajorSternal.INSTANCE, PectoralisMinor.INSTANCE,
                SerratusAnterior.INSTANCE, RectusAbdominis.INSTANCE, ExternalOblique.INSTANCE,
                InternalOblique.INSTANCE, TrapeziusUpper.INSTANCE, TrapeziusMiddle.INSTANCE, TrapeziusLower.INSTANCE,
                LatissimusDorsi.INSTANCE, LevatorScapulae.INSTANCE, Subclavius.INSTANCE, TransversusThoracis.INSTANCE,
                Diaphragm.INSTANCE, SerratusPosteriorSuperior.INSTANCE, SerratusPosteriorInferior.INSTANCE,
                TransversusAbdominis.INSTANCE, Pyramidalis.INSTANCE, QuadratusLumborum.INSTANCE,
                RhomboidMinor.INSTANCE, RhomboidMajor.INSTANCE, Iliocostalis.INSTANCE, Longissimus.INSTANCE,
                Spinalis.INSTANCE, SemispinalisThoracis.INSTANCE, SemispinalisCervicis.INSTANCE, Multifidus.INSTANCE,
                Pubococcygeus.INSTANCE, Iliococcygeus.INSTANCE, Puborectalis.INSTANCE, Coccygeus.INSTANCE,
                ExternalAnalSphincter.INSTANCE, DeepTransversePerineal.INSTANCE,
                SuperficialTransversePerineal.INSTANCE, Ischiocavernosus.INSTANCE, Bulbospongiosus.INSTANCE,
                ExternalIntercostal1.INSTANCE, ExternalIntercostal2.INSTANCE, ExternalIntercostal3.INSTANCE,
                ExternalIntercostal4.INSTANCE, ExternalIntercostal5.INSTANCE, ExternalIntercostal6.INSTANCE,
                ExternalIntercostal7.INSTANCE, ExternalIntercostal8.INSTANCE, ExternalIntercostal9.INSTANCE,
                ExternalIntercostal10.INSTANCE, ExternalIntercostal11.INSTANCE, InternalIntercostal1.INSTANCE,
                InternalIntercostal2.INSTANCE, InternalIntercostal3.INSTANCE, InternalIntercostal4.INSTANCE,
                InternalIntercostal5.INSTANCE, InternalIntercostal6.INSTANCE, InternalIntercostal7.INSTANCE,
                InternalIntercostal8.INSTANCE, InternalIntercostal9.INSTANCE, InternalIntercostal10.INSTANCE,
                InternalIntercostal11.INSTANCE, LevatorCostarum1.INSTANCE, LevatorCostarum2.INSTANCE,
                LevatorCostarum3.INSTANCE, LevatorCostarum4.INSTANCE, LevatorCostarum5.INSTANCE,
                LevatorCostarum6.INSTANCE, LevatorCostarum7.INSTANCE, LevatorCostarum8.INSTANCE,
                LevatorCostarum9.INSTANCE, LevatorCostarum10.INSTANCE, LevatorCostarum11.INSTANCE,
                LevatorCostarum12.INSTANCE, RotatorT1.INSTANCE, RotatorT2.INSTANCE, RotatorT3.INSTANCE,
                RotatorT4.INSTANCE, RotatorT5.INSTANCE, RotatorT6.INSTANCE, RotatorT7.INSTANCE, RotatorT8.INSTANCE,
                RotatorT9.INSTANCE, RotatorT10.INSTANCE, RotatorT11.INSTANCE, InterspinalisC2.INSTANCE,
                InterspinalisC3.INSTANCE, InterspinalisC4.INSTANCE, InterspinalisC5.INSTANCE, InterspinalisC6.INSTANCE,
                InterspinalisT12.INSTANCE, InterspinalisL1.INSTANCE, InterspinalisL2.INSTANCE,
                InterspinalisL3.INSTANCE, InterspinalisL4.INSTANCE, IntertransversariusC2.INSTANCE,
                IntertransversariusC3.INSTANCE, IntertransversariusC4.INSTANCE, IntertransversariusC5.INSTANCE,
                IntertransversariusC6.INSTANCE, IntertransversariusL1.INSTANCE, IntertransversariusL2.INSTANCE,
                IntertransversariusL3.INSTANCE, IntertransversariusL4.INSTANCE);
    }
}

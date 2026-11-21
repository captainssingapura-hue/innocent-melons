package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The shoulder's, the arm's and the hand's muscles, from the shoulder down: the deltoid's three parts, the rotator cuff
 * and the teres major; the upper arm's, the biceps, the brachialis, the coracobrachialis, the triceps; the forearm's,
 * its extensors on the back and outer side, its flexors on the front and inner side, the thumb's; the hand's, the
 * thumb's ball and the little finger's, the lumbricals and the interossei.
 */
public record ArmMuscles() implements StatelessFunctionalObject {

    public static final ArmMuscles INSTANCE = new ArmMuscles();

    public List<Muscle> muscles() {
        return List.of(DeltoidAnterior.INSTANCE, DeltoidMiddle.INSTANCE, DeltoidPosterior.INSTANCE,
                Supraspinatus.INSTANCE, Infraspinatus.INSTANCE, TeresMinor.INSTANCE, TeresMajor.INSTANCE,
                BicepsLongHead.INSTANCE, BicepsShortHead.INSTANCE, Brachialis.INSTANCE, Coracobrachialis.INSTANCE,
                TricepsLongHead.INSTANCE, TricepsLateralHead.INSTANCE, TricepsMedialHead.INSTANCE,
                Brachioradialis.INSTANCE, ExtensorCarpiRadialisLongus.INSTANCE, ExtensorCarpiRadialisBrevis.INSTANCE,
                ExtensorDigitorum.INSTANCE, ExtensorCarpiUlnaris.INSTANCE, Anconeus.INSTANCE, PronatorTeres.INSTANCE,
                FlexorCarpiRadialis.INSTANCE, PalmarisLongus.INSTANCE, FlexorCarpiUlnaris.INSTANCE,
                FlexorDigitorumSuperficialis.INSTANCE, Subscapularis.INSTANCE, Supinator.INSTANCE,
                FlexorDigitorumProfundus.INSTANCE, FlexorPollicisLongus.INSTANCE, AbductorPollicisLongus.INSTANCE,
                ExtensorPollicisBrevis.INSTANCE, ExtensorPollicisLongus.INSTANCE, PronatorQuadratus.INSTANCE,
                AdductorPollicis.INSTANCE, FirstDorsalInterosseous.INSTANCE, ExtensorIndicis.INSTANCE,
                ExtensorDigitiMinimi.INSTANCE, AbductorPollicisBrevis.INSTANCE, FlexorPollicisBrevis.INSTANCE,
                OpponensPollicis.INSTANCE, AbductorDigitiMinimiManus.INSTANCE, FlexorDigitiMinimiBrevis.INSTANCE,
                OpponensDigitiMinimi.INSTANCE, PalmarisBrevis.INSTANCE, Lumbrical1.INSTANCE, Lumbrical2.INSTANCE,
                Lumbrical3.INSTANCE, Lumbrical4.INSTANCE, DorsalInterosseous2.INSTANCE, DorsalInterosseous3.INSTANCE,
                DorsalInterosseous4.INSTANCE, PalmarInterosseous1.INSTANCE, PalmarInterosseous2.INSTANCE,
                PalmarInterosseous3.INSTANCE);
    }
}

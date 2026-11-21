package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The hip's, the leg's and the foot's muscles, from the hip down: the gluteals, the tensor, the iliacus and the psoas,
 * the hip's deep rotators, the obturators and the gemelli, the pelvic floor's; the thigh's, its band, the sartorius,
 * the quadriceps, the hamstrings, the adductors; the lower leg's, the calf's and the shin's, and their deep ones; the
 * foot's, on its top and in its sole.
 */
public record LegMuscles() implements StatelessFunctionalObject {

    public static final LegMuscles INSTANCE = new LegMuscles();

    public List<Muscle> muscles() {
        return List.of(GluteusMaximus.INSTANCE, GluteusMedius.INSTANCE, TensorFasciaeLatae.INSTANCE, Iliacus.INSTANCE,
                IliotibialBand.INSTANCE, Sartorius.INSTANCE, RectusFemoris.INSTANCE, VastusLateralis.INSTANCE,
                VastusMedialis.INSTANCE, VastusIntermedius.INSTANCE, BicepsFemoris.INSTANCE, Semitendinosus.INSTANCE,
                Semimembranosus.INSTANCE, AdductorLongus.INSTANCE, AdductorMagnus.INSTANCE, Gracilis.INSTANCE,
                GastrocnemiusMedial.INSTANCE, GastrocnemiusLateral.INSTANCE, Soleus.INSTANCE, AchillesTendon.INSTANCE,
                TibialisAnterior.INSTANCE, ExtensorDigitorumLongus.INSTANCE, FibularisLongus.INSTANCE,
                FibularisBrevis.INSTANCE, GluteusMinimus.INSTANCE, Piriformis.INSTANCE, QuadratusFemoris.INSTANCE,
                Psoas.INSTANCE, Pectineus.INSTANCE, AdductorBrevis.INSTANCE, BicepsFemorisShortHead.INSTANCE,
                Popliteus.INSTANCE, TibialisPosterior.INSTANCE, FlexorDigitorumLongus.INSTANCE,
                FlexorHallucisLongus.INSTANCE, ExtensorHallucisLongus.INSTANCE, AbductorHallucis.INSTANCE,
                FlexorDigitorumBrevis.INSTANCE, AbductorDigitiMinimiPedis.INSTANCE, ExtensorDigitorumBrevis.INSTANCE,
                ObturatorInternus.INSTANCE, ObturatorExternus.INSTANCE, GemellusSuperior.INSTANCE,
                GemellusInferior.INSTANCE, Plantaris.INSTANCE, FibularisTertius.INSTANCE,
                ExtensorHallucisBrevis.INSTANCE, QuadratusPlantae.INSTANCE, LumbricalPedis1.INSTANCE,
                LumbricalPedis2.INSTANCE, LumbricalPedis3.INSTANCE, LumbricalPedis4.INSTANCE,
                FlexorHallucisBrevis.INSTANCE, AdductorHallucis.INSTANCE, FlexorDigitiMinimiBrevisPedis.INSTANCE,
                PlantarInterosseous1.INSTANCE, PlantarInterosseous2.INSTANCE, PlantarInterosseous3.INSTANCE,
                DorsalInterosseousPedis1.INSTANCE, DorsalInterosseousPedis2.INSTANCE,
                DorsalInterosseousPedis3.INSTANCE, DorsalInterosseousPedis4.INSTANCE);
    }
}

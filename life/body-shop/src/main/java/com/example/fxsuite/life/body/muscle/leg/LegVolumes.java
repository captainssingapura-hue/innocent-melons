package com.example.fxsuite.life.body.muscle.leg;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;

/**
 * How big a leg's muscles are, as measured: the whole leg's muscle, cm3, 47.0 times the body's height (m) and mass (kg)
 * and 1290 more; each muscle a line in that, so much of it and so much more (Handsfield et al. 2014, MRI of 24 young
 * adults, 145 to 188 cm, 47 to 107 kg: the lines of their dissertation's appendix, table 3.2; the whole's fitted again
 * from their table 3.1). The peroneals measured together, shared as the fibularis longus and brevis are by the Rajagopal
 * model's volumes, seven tenths and three; the long extensors likewise, two thirds the toes' and one the great toe's; the
 * small external rotators, the gemelli, nine twentieths the upper and eleven the lower. The plantaris not measured:
 * estimated, a thousandth of the leg's muscle, some 7 cm3 in their mean subject, a few grams as cadavers weigh it, under
 * a hundredth of the calf's. One leg's.
 */
public record LegVolumes() implements StatelessFunctionalObject {

    public static final LegVolumes INSTANCE = new LegVolumes();

    /** The whole of one leg's muscle, cm3, for a body {@code height} m tall and {@code mass} kg. */
    public double whole(final double height, final double mass) {
        return 47.0 * height * mass + 1290;
    }

    /** Muscle {@code name}'s volume, cm3, in a leg of {@code whole} cm3 of muscle; none if it was not measured. */
    public Optional<Double> volume(final String name, final double whole) {
        final double[] f = line(name);
        return f.length == 0 ? Optional.empty() : Optional.of(Math.max(f[2] * (f[0] * whole + f[1]), 1));
    }

    /** Muscle {@code name}'s line: so much of the whole, so much more, and its share of what was measured with it. */
    private double[] line(final String name) {
        return switch (name) {
            case "gluteus maximus" -> new double[] {0.123, -25.4, 1};
            case "adductor magnus" -> new double[] {0.0793, -4.70, 1};
            case "gluteus medius" -> new double[] {0.0478, -16.9, 1};
            case "psoas major" -> new double[] {0.0550, -117, 1};
            case "iliacus" -> new double[] {0.0248, 0.383, 1};
            case "sartorius" -> new double[] {0.0256, -18.2, 1};
            case "adductor longus" -> new double[] {0.0259, -21.9, 1};
            case "gluteus minimus" -> new double[] {0.0129, 12.6, 1};
            case "adductor brevis" -> new double[] {0.0137, 6.20, 1};
            case "gracilis" -> new double[] {0.0138, 6.07, 1};
            case "pectineus" -> new double[] {0.0107, -9.94, 1};
            case "tensor fasciae latae" -> new double[] {0.0136, -31.5, 1};
            case "piriformis" -> new double[] {0.00372, 16.2, 1};
            case "quadratus femoris" -> new double[] {0.00475, -1.45, 1};
            case "vastus lateralis" -> new double[] {0.125, -55.7, 1};
            case "vastus medialis" -> new double[] {0.0631, -16.3, 1};
            case "vastus intermedius" -> new double[] {0.0273, 76.5, 1};
            case "rectus femoris" -> new double[] {0.0371, 4.97, 1};
            case "semimembranosus" -> new double[] {0.0319, 18.2, 1};
            case "biceps femoris" -> new double[] {0.0256, 24.3, 1};
            case "semitendinosus" -> new double[] {0.0285, -16.8, 1};
            case "biceps femoris, short head" -> new double[] {0.0160, -13.8, 1};
            case "popliteus" -> new double[] {0.00298, 2.11, 1};
            case "obturator externus" -> new double[] {0.00349, 28.3, 1};
            case "obturator internus" -> new double[] {0.00252, 8.68, 1};
            case "gemellus superior" -> new double[] {0.00172, 3.85, 0.45};
            case "gemellus inferior" -> new double[] {0.00172, 3.85, 0.55};
            default -> lower(name);
        };
    }

    /** The lower leg's lines. */
    private double[] lower(final String name) {
        return switch (name) {
            case "soleus" -> new double[] {0.0507, 78.2, 1};
            case "gastrocnemius, medial head" -> new double[] {0.0348, 9.42, 1};
            case "gastrocnemius, lateral head" -> new double[] {0.0199, 8.21, 1};
            case "tibialis anterior" -> new double[] {0.0161, 20.3, 1};
            case "fibularis longus" -> new double[] {0.0194, -7.43, 0.71};
            case "fibularis brevis" -> new double[] {0.0194, -7.43, 0.29};
            case "tibialis posterior" -> new double[] {0.0104, 30.8, 1};
            case "extensor digitorum longus" -> new double[] {0.0132, 8.70, 0.66};
            case "extensor hallucis longus" -> new double[] {0.0132, 8.70, 0.34};
            case "flexor hallucis longus" -> new double[] {0.0137, -18.9, 1};
            case "flexor digitorum longus" -> new double[] {0.00259, 11.6, 1};
            case "plantaris" -> new double[] {0.001, 0, 1};
            default -> new double[0];
        };
    }
}

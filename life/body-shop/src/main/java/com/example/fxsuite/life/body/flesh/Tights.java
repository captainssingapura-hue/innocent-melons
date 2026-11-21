package com.example.fxsuite.life.body.flesh;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * Tights: cloth as close as skin, from the waist to the ankles, over the hips and down both legs; so thin, its
 * waistband a little thicker. Cut from the skin it is on ({@link Tailor}), so it moves as the skin does.
 *
 * @param colour    its colour, as {@code #rrggbb}
 * @param thickness how thick the cloth is, cm
 * @param waist     the landmark its waistband is cut at
 * @param off       how far above that landmark, cm, in the drawn figure's centimetres
 */
public record Tights(String colour, double thickness, Landmark waist, double off) implements Scalar {

    /** Plain black tights (a dark charcoal, so that the light shows their shape), the waistband under the navel. */
    public static final Tights BLACK = new Tights("#2a2b33", 0.12, Landmark.NAVEL, -1.5);

    /** How much thicker its waistband is than the rest, cm; and how deep it is. */
    public double band() {
        return 0.15;
    }

    public double bandDeep() {
        return 3.0;
    }
}

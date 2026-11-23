package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Proportions;

/**
 * What a rib's shape is said by that changes with who it is, as Holcombe, Wang and Grotberg found it in 1,042 adults'
 * CT (J Anat 2017, 231:229, table A2): a sum of a number, and so much a year of age, for a woman, a metre of height and
 * a kilogram of weight, each as printed and scaled as the table says. For the body as drawn: its height and weight
 * ({@link Proportions#DRAWN}), {@link #AGE} years old.
 */
public enum RibMeasure {
    /** How long it is, end to end, its head to where its cartilage begins, mm. */
    CHORD(1, 1e-3, 1e-1, 1e-1, 1e-3),
    /** How far out of the line from end to end it reaches at most, in lengths end to end. */
    PEAK(1e-2, 1e-5, 1e-3, 1e-3, 1e-5),
    /** How far up from hanging straight down its line from end to end is turned, degrees. */
    PUMP(1e-1, 1e-3, 1e-2, 1e-1, 1e-3);

    /** How old the body as drawn is taken to be, years: a young man. */
    private static final double AGE = 30;

    private final double[] scale;

    RibMeasure(final double... scale) {
        this.scale = scale;
    }

    /** This measure, from {@code row}, the table's for it, as printed, for the body as drawn, as female as said. */
    public double of(final int[] row, final double female) {
        final Proportions p = Proportions.DRAWN;
        final double[] by = {1, AGE, female, p.stature() / 100, p.mass()};
        double sum = 0;
        for (int k = 0; k < by.length; k++) {
            sum += row[k] * scale[k] * by[k];
        }
        return sum;
    }
}

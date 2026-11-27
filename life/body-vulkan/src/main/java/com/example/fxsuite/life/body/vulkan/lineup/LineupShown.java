package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Proportions;

import java.util.ArrayList;
import java.util.List;

/**
 * What the line-up has on show: how tall each of its figures stands, cm, in their order along it, left to right as
 * they face, and what each is drawn in ({@link Paintwork}); and which of them is picked, to be looked at, made taller
 * or shorter and painted. Each figure is made at the model's height ({@link Proportions#MODEL}) and drawn larger or
 * smaller, whole, to stand as tall as said: its stride and its stance with it.
 *
 * @param tall   how tall each stands, cm
 * @param paint  what each is drawn in
 * @param picked which is picked, counted from the first
 */
public record LineupShown(List<Double> tall, List<Paintwork> paint, int picked) {

    /** The shortest and the tallest a figure may stand, cm. */
    public static final double SHORTEST = 120;
    public static final double TALLEST = 240;
    /** How far apart their middles stand along the line, cm, at the model's height. */
    private static final double APART = 110;

    public LineupShown {
        tall = List.copyOf(tall);
        paint = List.copyOf(paint);
        if (tall.isEmpty()) {
            throw new IllegalArgumentException("a line-up of nobody");
        }
        if (paint.size() != tall.size()) {
            throw new IllegalArgumentException(paint.size() + " paintworks for " + tall.size() + " figures");
        }
        if (picked < 0 || picked >= tall.size()) {
            throw new IllegalArgumentException("picked " + picked + " of " + tall.size());
        }
        for (final double t : tall) {
            if (!(t >= SHORTEST && t <= TALLEST)) {
                throw new IllegalArgumentException("a figure " + t + " cm tall, not between " + SHORTEST + " and "
                        + TALLEST);
            }
        }
    }

    /** A line-up of {@code count} figures, each as tall as the model and as built, the first picked. */
    public LineupShown(final int count) {
        this(java.util.Collections.nCopies(count, Proportions.MODEL.stature()),
                java.util.Collections.nCopies(count, new Paintwork()), 0);
    }

    /** This line-up with {@code i} picked. */
    public LineupShown picking(final int i) {
        return new LineupShown(tall, paint, i);
    }

    /** This line-up with the picked figure standing {@code cm} tall. */
    public LineupShown standing(final double cm) {
        final List<Double> t = new ArrayList<>(tall);
        t.set(picked, cm);
        return new LineupShown(t, paint, picked);
    }

    /** This line-up with the picked figure drawn in {@code work}. */
    public LineupShown painting(final Paintwork work) {
        final List<Paintwork> p = new ArrayList<>(paint);
        p.set(picked, work);
        return new LineupShown(tall, p, picked);
    }

    /** How many times as large as it was made figure {@code i} is drawn. */
    public double scale(final int i) {
        return tall.get(i) / Proportions.MODEL.stature();
    }

    /**
     * How far along the line figure {@code i}'s middle stands from the line's middle, cm: each as far from the next
     * as the larger of the two needs, the line's middle midway between its ends.
     */
    public double along(final int i) {
        final double[] at = new double[tall.size()];
        for (int k = 1; k < at.length; k++) {
            at[k] = at[k - 1] + APART * Math.max(scale(k - 1), scale(k));
        }
        return at[i] - at[at.length - 1] / 2;
    }
}

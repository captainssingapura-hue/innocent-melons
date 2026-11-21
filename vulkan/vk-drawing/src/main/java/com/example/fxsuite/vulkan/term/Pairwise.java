package com.example.fxsuite.vulkan.term;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

/**
 * What is done to two things number by number: three with three, or one with one. Most take a lone number with
 * three as well, the lone number used for each of the three, as the shader's language does; the two that the
 * language will not ({@link #ANGLE}, {@link #RAISED}) take only two of the same shape.
 */
public enum Pairwise implements Op {

    PLUS("(%s + %s)", Double::sum, true),
    LESS("(%s - %s)", (a, b) -> a - b, true),
    TIMES("(%s * %s)", (a, b) -> a * b, true),
    OVER("(%s / %s)", (a, b) -> a / b, true),
    /** The lesser of the two. */
    LEAST("min(%s, %s)", Math::min, true),
    /** The greater of the two. */
    MOST("max(%s, %s)", Math::max, true),
    /** Whether the second has come as far as the first: 1 where it has, 0 where it is still short of it. */
    PAST("step(%s, %s)", (edge, number) -> number < edge ? 0 : 1, true),
    /** The angle whose tangent is the first over the second, all the way round: from -pi to pi. */
    ANGLE("atan(%s, %s)", Math::atan2, false),
    /** The first to the power of the second. */
    RAISED("pow(%s, %s)", Math::pow, false);

    private final String form;
    private final DoubleBinaryOperator works;
    private final boolean spreads;

    Pairwise(final String form, final DoubleBinaryOperator works, final boolean spreads) {
        this.form = form;
        this.works = works;
        this.spreads = spreads;
    }

    @Override
    public int size(final List<Integer> of) {
        if (of.size() != 2) {
            throw new IllegalArgumentException(this + " is done to two things, not " + of.size());
        }
        final int first = of.get(0);
        final int second = of.get(1);
        final boolean lone = first == 1 || second == 1;
        if (first != second && !(spreads && lone)) {
            throw new IllegalArgumentException(this + " cannot be done to " + first + " numbers and " + second);
        }
        return Math.max(first, second);
    }

    @Override
    public double[] worked(final List<double[]> of) {
        final double[] first = of.get(0);
        final double[] second = of.get(1);
        final double[] made = new double[Math.max(first.length, second.length)];
        for (int k = 0; k < made.length; k++) {
            made[k] = works.applyAsDouble(first[first.length == 1 ? 0 : k], second[second.length == 1 ? 0 : k]);
        }
        return made;
    }

    @Override
    public String written(final List<String> of) {
        return form.formatted(of.get(0), of.get(1));
    }
}

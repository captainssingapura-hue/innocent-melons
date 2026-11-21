package com.example.fxsuite.vulkan.term;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

/** What is done to one thing, number by number: one number or three, the same shape coming of it. */
public enum Single implements Op {

    /** The whole number at or below it. */
    FLOORED("floor(%s)", Math::floor),
    /** What is left of it over the whole number at or below it: from 0 up to, not reaching, 1. */
    PART("fract(%s)", number -> number - Math.floor(number)),
    SINE("sin(%s)", Math::sin),
    COSINE("cos(%s)", Math::cos),
    /** The angle whose cosine it is: from 0 to pi. */
    ARC("acos(%s)", Math::acos),
    ROOT("sqrt(%s)", Math::sqrt),
    NEGATED("(-%s)", number -> -number);

    private final String form;
    private final DoubleUnaryOperator works;

    Single(final String form, final DoubleUnaryOperator works) {
        this.form = form;
        this.works = works;
    }

    @Override
    public int size(final List<Integer> of) {
        if (of.size() != 1) {
            throw new IllegalArgumentException(this + " is done to one thing, not " + of.size());
        }
        return of.get(0);
    }

    @Override
    public double[] worked(final List<double[]> of) {
        final double[] made = new double[of.get(0).length];
        for (int k = 0; k < made.length; k++) {
            made[k] = works.applyAsDouble(of.get(0)[k]);
        }
        return made;
    }

    @Override
    public String written(final List<String> of) {
        return form.formatted(of.get(0));
    }
}

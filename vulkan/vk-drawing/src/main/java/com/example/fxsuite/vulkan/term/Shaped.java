package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * What is done between three numbers and one: three made of ones, one taken from three, and what is done to three as
 * a way or a place rather than number by number.
 */
public enum Shaped implements Op {

    /** Three numbers as one thing of three. */
    JOINED("vec3(%s, %s, %s)", List.of(1, 1, 1), 3),
    /** The first of three; its second; its third. */
    FIRST("%s.x", List.of(3), 1),
    SECOND("%s.y", List.of(3), 1),
    THIRD("%s.z", List.of(3), 1),
    /** How much of each of two ways lies along the other. */
    DOTTED("dot(%s, %s)", List.of(3, 3), 1),
    /** The same way, one long. */
    UNIT("normalize(%s)", List.of(3), 3),
    /** Between two things of three: the first where the share is nothing, the second where it is one. */
    MIXED("mix(%s, %s, %s)", List.of(3, 3, 1), 3);

    private final String form;
    private final List<Integer> takes;
    private final int makes;

    Shaped(final String form, final List<Integer> takes, final int makes) {
        this.form = form;
        this.takes = takes;
        this.makes = makes;
    }

    @Override
    public int size(final List<Integer> of) {
        if (!of.equals(takes)) {
            throw new IllegalArgumentException(this + " is done to things of " + takes + " numbers, not " + of);
        }
        return makes;
    }

    @Override
    public double[] worked(final List<double[]> of) {
        final double[] first = of.get(0);
        return switch (this) {
            case JOINED -> new double[] {first[0], of.get(1)[0], of.get(2)[0]};
            case FIRST -> new double[] {first[0]};
            case SECOND -> new double[] {first[1]};
            case THIRD -> new double[] {first[2]};
            case DOTTED -> new double[] {dotted(first, of.get(1))};
            case UNIT -> scaled(first, 1 / Math.sqrt(dotted(first, first)));
            case MIXED -> mixed(first, of.get(1), of.get(2)[0]);
        };
    }

    @Override
    public String written(final List<String> of) {
        return form.formatted(of.toArray());
    }

    private double dotted(final double[] a, final double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private double[] scaled(final double[] of, final double by) {
        return new double[] {of[0] * by, of[1] * by, of[2] * by};
    }

    private double[] mixed(final double[] from, final double[] to, final double share) {
        return new double[] {from[0] + (to[0] - from[0]) * share, from[1] + (to[1] - from[1]) * share,
                from[2] + (to[2] - from[2]) * share};
    }
}

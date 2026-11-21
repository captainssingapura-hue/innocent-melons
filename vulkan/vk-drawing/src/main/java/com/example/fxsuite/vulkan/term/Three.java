package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * Three numbers in an expression, and what can be done to any three. Not for a term to use: a term says a colour, a
 * place or a way ({@link Colour}, {@link Point}, {@link Direction}), each of which lets through only what makes
 * sense of it.
 */
record Three(Node node) {

    Three {
        if (node.size() != 3) {
            throw new IllegalArgumentException("three numbers, not " + node.size());
        }
    }

    Three(final double first, final double second, final double third) {
        this(new Constant(new double[] {first, second, third}));
    }

    Three plus(final Three other) {
        return new Three(new Applied(Pairwise.PLUS, List.of(node, other.node)));
    }

    Three less(final Three other) {
        return new Three(new Applied(Pairwise.LESS, List.of(node, other.node)));
    }

    /** Each of its numbers times the same one of {@code other}'s. */
    Three times(final Three other) {
        return new Three(new Applied(Pairwise.TIMES, List.of(node, other.node)));
    }

    /** Each of its numbers times {@code by}. */
    Three scaled(final Real by) {
        return new Three(new Applied(Pairwise.TIMES, List.of(node, by.node())));
    }

    Three negated() {
        return new Three(new Applied(Single.NEGATED, List.of(node)));
    }

    Three unit() {
        return new Three(new Applied(Shaped.UNIT, List.of(node)));
    }

    Three mixed(final Three towards, final Real share) {
        return new Three(new Applied(Shaped.MIXED, List.of(node, towards.node, share.node())));
    }

    Real dot(final Three other) {
        return new Real(new Applied(Shaped.DOTTED, List.of(node, other.node)));
    }

    Real picked(final Shaped which) {
        return new Real(new Applied(which, List.of(node)));
    }
}

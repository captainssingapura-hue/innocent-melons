package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * A number in an expression: a length, a share, an angle, whatever its term has it mean. What can be done to a number
 * is done here, each making another; nothing here can be done to a colour or a way, which have their own.
 */
public record Real(Node node) {

    public Real {
        if (node.size() != 1) {
            throw new IllegalArgumentException("a number is one number, not " + node.size());
        }
    }

    /** The number {@code number}, wherever a point is drawn. */
    public Real(final double number) {
        this(new Constant(new double[] {number}));
    }

    public Real plus(final Real other) {
        return both(Pairwise.PLUS, other);
    }

    public Real less(final Real other) {
        return both(Pairwise.LESS, other);
    }

    public Real times(final Real other) {
        return both(Pairwise.TIMES, other);
    }

    public Real times(final double other) {
        return times(new Real(other));
    }

    public Real over(final Real other) {
        return both(Pairwise.OVER, other);
    }

    public Real over(final double other) {
        return over(new Real(other));
    }

    /** No more than {@code other}: the lesser of the two. */
    public Real least(final Real other) {
        return both(Pairwise.LEAST, other);
    }

    /** No less than {@code other}: the greater of the two. */
    public Real most(final Real other) {
        return both(Pairwise.MOST, other);
    }

    /** Whether it has come as far as {@code edge}: 1 where it has, 0 where it is still short of it. */
    public Real past(final Real edge) {
        return new Real(new Applied(Pairwise.PAST, List.of(edge.node, node)));
    }

    /** This where {@code share} is nothing, {@code towards} where it is one, and between them between. */
    public Real mixed(final Real towards, final Real share) {
        return plus(towards.less(this).times(share));
    }

    /** Held between {@code low} and {@code high}. */
    public Real held(final double low, final double high) {
        return most(new Real(low)).least(new Real(high));
    }

    /** The angle whose tangent is this over {@code other}, all the way round: from -pi to pi. */
    public Real angle(final Real other) {
        return both(Pairwise.ANGLE, other);
    }

    /** This to the power of {@code power}. */
    public Real raised(final double power) {
        return both(Pairwise.RAISED, new Real(power));
    }

    /** The whole number at or below it. */
    public Real floored() {
        return alone(Single.FLOORED);
    }

    /** What is left of it over the whole number at or below it. */
    public Real part() {
        return alone(Single.PART);
    }

    public Real sine() {
        return alone(Single.SINE);
    }

    /** The angle whose cosine it is: from 0 to pi. */
    public Real arc() {
        return alone(Single.ARC);
    }

    public Real root() {
        return alone(Single.ROOT);
    }

    private Real both(final Pairwise op, final Real other) {
        return new Real(new Applied(op, List.of(node, other.node)));
    }

    private Real alone(final Single op) {
        return new Real(new Applied(op, List.of(node)));
    }
}

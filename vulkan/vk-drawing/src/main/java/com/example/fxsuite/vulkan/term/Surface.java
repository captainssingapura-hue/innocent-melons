package com.example.fxsuite.vulkan.term;

import java.util.ArrayList;
import java.util.List;

/**
 * How a surface is coloured before any light falls on it: a colour underneath, and terms laid over it in order, each
 * showing as much as its share says; how it shines, the colour of its highlight and how tight the highlight is; and
 * what light it gives of its own, which is none for most things.
 * One description for every distance: seen close, every term in full; further off, each term fading to its average
 * as a pixel comes to cover more than its features ({@link Turns#pulse}); and for what is far enough, a coarser
 * surface made from this one, with what can no longer be seen worked out once and left out of the shader
 * ({@link #coarser}).
 */
public record Surface(Colour base, List<Term> terms, Colour shine, double tightness, Colour glow) {

    /** A term is taken to have faded altogether once a pixel covers this many of its smallest feature. */
    private static final double FADED = 8;

    public Surface {
        terms = List.copyOf(terms);
    }

    /** A surface that gives no light of its own. */
    public Surface(final Colour base, final List<Term> terms, final Colour shine, final double tightness) {
        this(base, terms, shine, tightness, new Colour(0, 0, 0));
    }

    /** The same surface, giving {@code light} of its own besides: it shows that much whatever light falls on it. */
    public Surface glowing(final Colour light) {
        return new Surface(base, terms, shine, tightness, light);
    }

    /** Its colour at a point, unlit: the terms laid over the base, what is of constants alone worked out already. */
    public Colour colour() {
        Colour colour = base;
        for (final Term term : terms) {
            colour = colour.mixed(term.colour(), term.shown());
        }
        return new Colour(colour.node().folded());
    }

    /**
     * The surface for where a pixel covers {@code covers} centimetres of it or more: each term whose features are
     * by then too small to see showing its average everywhere it belongs, the average worked out over the thing by
     * {@code averaged}. A term that still shows is kept as it is.
     */
    public Surface coarser(final double covers, final Averaged averaged) {
        return new Surface(base, terms.stream()
                .map(term -> covers >= FADED * term.smallest() ? term.evened(average(term, averaged)) : term)
                .toList(), shine, tightness, glow);
    }

    /**
     * Two surfaces as one: this where {@code share} is one, {@code under} where it is nothing, as a wall stands over
     * its footing. Each keeps its own terms, each belonging to its own part; the highlight is as tight as this one's.
     */
    public Surface above(final Surface under, final Real share) {
        final Real rest = new Real(1).less(share);
        final List<Term> all = new ArrayList<>();
        under.terms.forEach(term -> all.add(term.within(rest)));
        terms.forEach(term -> all.add(term.within(share)));
        return new Surface(under.base.mixed(base, share), all, under.shine.mixed(shine, share), tightness,
                under.glow.mixed(glow, share));
    }

    /** What {@code term} comes to over the part of the thing it belongs to. */
    private double average(final Term term, final Averaged averaged) {
        final double part = averaged.of(term.within());
        return part > 0 ? averaged.of(term.shown()) / part : 0;
    }
}

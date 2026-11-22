package com.example.fxsuite.vulkan.term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * How a surface is coloured before any light falls on it: a colour underneath, and terms laid over it in order, each
 * showing as much as its share says; how it shines, the colour of its highlight and how tight the highlight is; and
 * what light it gives of its own and how much it mirrors its surroundings, each of which is nothing for most
 * things; for what has a clear hard skin, how much more it mirrors when it is seen at a glancing angle; for
 * a paint with metal in it, the flakes that lie in it; and, for what is seen through, how much of what is behind
 * it shows.
 * One description for every distance: seen close, every term in full; further off, each term fading to its average
 * as a pixel comes to cover more than its features ({@link Turns#pulse}); and for what is far enough, a coarser
 * surface made from this one, with what can no longer be seen worked out once and left out of the shader
 * ({@link #coarser}).
 */
public record Surface(Colour base, List<Term> terms, Colour shine, double tightness, Colour glow, Colour mirror,
                      Optional<Colour> glance, Optional<Flakes> flakes, double through) {

    /** A term is taken to have faded altogether once a pixel covers this many of its smallest feature. */
    private static final double FADED = 8;

    public Surface {
        terms = List.copyOf(terms);
        if (!(through >= 0 && through <= 1)) {
            throw new IllegalArgumentException("a surface lets through none, some or all of what is behind it: "
                    + through);
        }
    }

    /**
     * The same surface, seen through, as a pane of glass is: of what is behind it, {@code share} shows where the
     * surface is seen square on, and less the more nearly it is seen edge on, as it comes to mirror more. Its own
     * colour shows only as much as is not let through; its shine and what it mirrors show in full. A surface
     * never told this hides whatever is behind it.
     */
    public Surface clear(final double share) {
        return new Surface(base, terms, shine, tightness, glow, mirror, glance, flakes, share);
    }

    /**
     * The same surface with {@code mark} laid over it besides, over all its own terms: a line painted on it, a
     * stain. The mark fades to its average far off as every term does.
     */
    public Surface marked(final Term mark) {
        final List<Term> all = new ArrayList<>(terms);
        all.add(mark);
        return new Surface(base, all, shine, tightness, glow, mirror, glance, flakes, through);
    }

    /** Whether anything behind it shows through it. */
    public boolean seenThrough() {
        return through > 0;
    }

    /** A surface that gives no light of its own. */
    public Surface(final Colour base, final List<Term> terms, final Colour shine, final double tightness) {
        this(base, terms, shine, tightness, new Colour(0, 0, 0), new Colour(0, 0, 0), Optional.empty(),
                Optional.empty(), 0);
    }

    /**
     * The same surface with flakes of metal lying in it, as a metallic paint has: their wide, soft shine besides
     * its own, glittering seen close, and its colour duller seen edge on.
     */
    public Surface flaked(final Flakes lying) {
        return new Surface(base, terms, shine, tightness, glow, mirror, glance, Optional.of(lying), through);
    }

    /**
     * The same surface, mirroring its surroundings besides, as polished metal does: of the light that comes to it
     * from the sky and the ground it sends on {@code share}, as much of the red, the green and the blue as that
     * says, so gold sends on gold.
     */
    public Surface mirroring(final Colour share) {
        return new Surface(base, terms, shine, tightness, glow, share, glance, flakes, through);
    }

    /**
     * The same surface, mirroring more of its surroundings the more nearly it is seen edge on, as anything with a
     * clear, hard skin does, a lacquered sheet or a pane of glass: seen square on it sends on what its mirroring
     * says, and seen at a glancing angle {@code atGlance}, going from the one to the other quickly near the edge.
     * A surface never told this mirrors the same from every side.
     */
    public Surface glancing(final Colour atGlance) {
        return new Surface(base, terms, shine, tightness, glow, mirror, Optional.of(atGlance), flakes, through);
    }

    /** The same surface, giving {@code light} of its own besides: it shows that much whatever light falls on it. */
    public Surface glowing(final Colour light) {
        return new Surface(base, terms, shine, tightness, light, mirror, glance, flakes, through);
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
                .toList(), shine, tightness, glow, mirror, glance, flakes, through);
    }

    /**
     * Two surfaces as one: this where {@code share} is one, {@code under} where it is nothing, as a wall stands over
     * its footing. Each keeps its own terms, each belonging to its own part; the highlight is as tight as this one's.
     * Neither may have flakes lying in it or be seen through: both are of a whole surface, and are refused of a
     * part of one.
     */
    public Surface above(final Surface under, final Real share) {
        if (flakes.isPresent() || under.flakes.isPresent()) {
            throw new IllegalArgumentException("a surface with flakes in it stands alone, not over or under another");
        }
        if (seenThrough() || under.seenThrough()) {
            throw new IllegalArgumentException("a surface seen through stands alone, not over or under another");
        }
        final Real rest = new Real(1).less(share);
        final List<Term> all = new ArrayList<>();
        under.terms.forEach(term -> all.add(term.within(rest)));
        terms.forEach(term -> all.add(term.within(share)));
        // seen at a glance, each part mirrors what it would by itself: as square on, where it was never told more
        final Optional<Colour> atGlance = glance.isEmpty() && under.glance.isEmpty() ? Optional.empty()
                : Optional.of(under.glance.orElse(under.mirror).mixed(glance.orElse(mirror), share));
        return new Surface(under.base.mixed(base, share), all, under.shine.mixed(shine, share), tightness,
                under.glow.mixed(glow, share), under.mirror.mixed(mirror, share), atGlance, Optional.empty(), 0);
    }

    /** What {@code term} comes to over the part of the thing it belongs to. */
    private double average(final Term term, final Averaged averaged) {
        final double part = averaged.of(term.within());
        return part > 0 ? averaged.of(term.shown()) / part : 0;
    }
}

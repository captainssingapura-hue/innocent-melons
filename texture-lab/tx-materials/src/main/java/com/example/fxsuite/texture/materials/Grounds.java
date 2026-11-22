package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Patches;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The kinds of ground the land is covered with, each with its surface: the least there can be and still build a
 * landscape. Every one is made the same way, of three things at three sizes, so that nothing is seen to repeat
 * however far the ground runs:
 *
 * <ul>
 *   <li>drifts, some metres across: the whole going over, here and there, toward another colour, as turf is drier
 *       on a rise;</li>
 *   <li>patches: each place its own share of a darker colour. Where the ground is of separate things, stones or
 *       blocks or plates of dried mud, the patches have edges and no two are one shape; where it is not, as turf
 *       and earth and sand are not, one patch goes over into the next;</li>
 *   <li>grains, the smallest: a few of them a colour of their own, as blades catch the light, or grit lies in
 *       sand.</li>
 * </ul>
 *
 * <p>Seen from further off the grains go first, then the patches, then the drifts, each to its average, until the
 * ground is one colour: which is how nearly all land is seen. Everything in it comes from numbers scattered over
 * cells, so that what is a paler fleck from a few paces can one day be a stone standing where the fleck was.
 *
 * <p>The soft patches and the grains are in cells that fill the space the ground lies in, not a sheet laid over it,
 * so they are not drawn out on a slope. The patches with edges are laid out on the level, and on a steep bank they
 * are drawn out down it. The sizes and colours are their author's, from no survey.
 */
public enum Grounds {
    /** Grass kept short by grazing or mowing: tufts, with blades that catch the light, drier on the rises. */
    TURF("#6f8f4a", "#587a3c", 45, false, 0.55, "#9ab368", 0.6, 0.22, "#94a05a", 900, 0),
    /** Bare earth: clods, with small stones in it, paler where it has dried. */
    TOPSOIL("#6b5540", "#54412f", 22, false, 0.6, "#9a8a74", 0.8, 0.1, "#7c654b", 700, 0),
    /** Sand: nearly even, with dark grit in it. */
    SAND("#d8c79c", "#c6b488", 70, false, 0.4, "#a08f6a", 0.15, 0.2, "#e3d6b2", 1200, 0),
    /** Gravel: stones a thumb across, no two one grey, a few of them pale. */
    GRAVEL("#9b968c", "#77736b", 4, true, 0.75, "#c9c4b8", 0.6, 0.2, "#8b877f", 600, 0),
    /** Rock as it lies in the ground: great blocks, each its own grey, speckled with lichen. */
    BEDROCK("#8a8780", "#6e6b66", 140, true, 0.7, "#a09e8c", 0.9, 0.07, "#7b7977", 800, 0),
    /** Mud: wet earth, cracked into plates, with a dull shine where the sun is mirrored. */
    MUD("#4f4336", "#3d342a", 30, true, 0.6, "#6a5c4b", 1.2, 0.1, "#5b4d3e", 500, 0.22);

    /** How far a drift goes toward its colour. */
    private static final double DRIFTED = 0.6;
    /** What tells the patches' own shades and the grains from the patches' middles, which take the first two. */
    private static final int MIDDLES = 31;
    private static final int SHADE = 33;
    private static final int GRAIN = 34;
    private static final double MIDDLE = 0.5;

    private final String base;
    private final String dark;
    private final double patch;
    private final boolean edged;
    private final double darkest;
    private final String fleck;
    private final double grain;
    private final double few;
    private final String drifted;
    private final double drift;
    private final double shine;

    Grounds(final String base, final String dark, final double patch, final boolean edged, final double darkest,
            final String fleck, final double grain, final double few, final String drifted, final double drift,
            final double shine) {
        this.base = base;
        this.dark = dark;
        this.patch = patch;
        this.edged = edged;
        this.darkest = darkest;
        this.fleck = fleck;
        this.grain = grain;
        this.few = few;
        this.drifted = drifted;
        this.drift = drift;
        this.shine = shine;
    }

    /** Its surface, worked out from where on the ground a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        // drifts wander, and are never in rows: two slow waves each way, one leaning on the other
        final Real drifts = at.across().over(drift).plus(at.along().over(drift * 1.7).sine()).sine()
                .times(at.along().over(drift * 0.8).plus(at.across().over(drift * 2.3).sine().times(1.3)).sine())
                .times(MIDDLE).plus(new Real(MIDDLE)).held(0, 1);
        final Real patches = edged
                ? new Patches(at.across(), at.along(), patch, MIDDLES).scatter(SHADE).settling(seen.covers())
                : new Mottle(patch, SHADE).soft(at, seen.covers());
        final Real grains = new Mottle(grain, GRAIN).cell(at, 0, 0, 0).some(few, seen.covers());
        return new Surface(new Colour(base), List.of(
                new Term("drifts", new Colour(drifted), drifts.times(DRIFTED), drift / 2),
                new Term("patches", new Colour(dark), patches.times(darkest), patch / 2),
                new Term("grains", new Colour(fleck), grains, grain)),
                new Colour(shine, shine, shine), 40);
    }

}

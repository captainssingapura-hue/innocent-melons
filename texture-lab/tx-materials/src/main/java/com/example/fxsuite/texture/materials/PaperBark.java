package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The bark of a white birch: chalky white and smooth on its trunk and its larger boughs, with grey bands where its
 * papery skin is peeling, short dark dashes lying across the wood (the pores it breathes through), and, low on the
 * trunk, black patches where the white has worn into rough dark bark. Its slender boughs and twigs are not white
 * but a dark reddish brown, the white coming in as the wood thickens. It is dull, with no shine.
 *
 * <p>It is laid out over where on its bough's bark a point is (across: how far round; up: how far along, each
 * bough set on by a stretch of its own, the trunk's nothing; and how thick the wood is there), so the dashes and
 * bands lie across the wood as a birch's do, and only the trunk is black at its foot. The bark has no depth to
 * catch the light, and does not peel: those wait for relief. Its colours and sizes are their author's, from no
 * measured tree.
 */
public record PaperBark() {

    private static final int PEEL = 71;
    private static final int PORES = 72;
    private static final int BLACK = 73;
    /** A pore's dash, in centimetres: how long round the bough, and how tall along it; and what share are dashes. */
    private static final double DASH = 2.6;
    private static final double TALL = 0.45;
    private static final double PORED = 0.09;
    /** How far a row of dashes is moved round from the row below it, of a dash's length. */
    private static final double STAGGER = 0.618;
    /** How far up the trunk its black foot reaches, in centimetres. */
    private static final double FOOT = 130;
    /** How thick the wood is, in centimetres from its middle, where it is wholly twig-brown, and where wholly white. */
    private static final double BROWN = 0.5;
    private static final double WHITE = 1.8;

    /** Its surface, worked out from where on the tree a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real covers = seen.covers();
        return new Surface(new Colour("#e7e3d9"), List.of(
                new Term("peeling", new Colour("#b9b2a4"),
                        new Mottle(5, PEEL).soft(at.scaled(0.35, 1.6, 1), covers).times(1.6).less(new Real(0.5))
                                .held(0, 1), 3),
                new Term("pores", new Colour("#2f2a26"),
                        pores(at).some(PORED, covers).times(new Real(1).less(slender(at))), TALL),
                new Term("black foot", new Colour("#24201d"), foot(at, covers), 2),
                new Term("slender wood", new Colour("#4d332b"), slender(at), WHITE)),
                new Colour(0, 0, 0), 8);
    }

    /** The cells the pores' dashes are in: long round the bough and short along it, each row moved round a little. */
    private Scatter pores(final Point at) {
        final Real row = at.up().over(TALL).floored();
        return new Scatter(at.across().over(DASH).plus(row.times(STAGGER)).floored(), row, PORES, TALL);
    }

    /**
     * How much of the black foot is at a point: patches near the ground, fading upward. Only the trunk begins its
     * way along the wood from nothing, so only the trunk has a foot.
     */
    private Real foot(final Point at, final Real covers) {
        final Real low = new Real(1).less(at.up().over(FOOT)).held(0, 1);
        final Real patches = new Mottle(3.5, BLACK).soft(at.scaled(1, 0.5, 1), covers).times(2.2)
                .less(new Real(0.9).less(low.times(0.6))).held(0, 1);
        return patches.times(low);
    }

    /** How brown the wood is for how slender it is: wholly where it is thin, none where it is thick. */
    private Real slender(final Point at) {
        return new Real(WHITE).less(at.along()).over(WHITE - BROWN).held(0, 1).times(at.along().past(new Real(0)));
    }
}

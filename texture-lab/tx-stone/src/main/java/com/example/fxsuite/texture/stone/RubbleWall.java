package com.example.fxsuite.texture.stone;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * A rubble wall (毛石墙): field stones as they come, no two of a shape, fitted together and bedded in mortar. What a
 * house of brick or of adobe commonly stands on, a few courses of it under the wall to keep the wall's foot out of
 * the wet. Its face is a surface of three terms over the paler stone:
 *
 * <ul>
 * <li>the stones, each its own share of a darker colour;</li>
 * <li>their grain, a few specks of every stone darkened;</li>
 * <li>the mortar, in the joints, over both.</li>
 * </ul>
 *
 * @param stone    about how far across a stone is, in centimetres
 * @param joint    about how thick the mortar is between stones, in centimetres
 * @param mortared the mortar's share of the whole wall, from nothing to one: worked out, unless it is given
 */
public record RubbleWall(double stone, double joint, Colour pale, Colour dark, Colour mortar, Colour grain,
                         double mortared) {

    /** What tells each scatter from the others: the stones' middles take the first two numbers. */
    private static final int SHADE = Jointing.MIDDLES + 2;
    private static final int GRAIN = Jointing.MIDDLES + 3;
    private static final double GRAINED = 0.5;
    private static final double GRAINS = 0.14;
    private static final double DARKENED = 0.4;

    /** Stones a hand and a half across, grey and tan, in a joint of lime and mud. */
    public RubbleWall() {
        this(24, 1.6);
    }

    /** Stones about {@code stone} across with about {@code joint} of mortar between, its share worked out by Java. */
    public RubbleWall(final double stone, final double joint) {
        this(stone, joint, new Colour("#aaa59b"), new Colour("#7b776e"), new Colour("#c6bca4"), new Colour("#4f4c46"),
                new Jointing(stone, joint).worked());
    }

    /** Its face: how it is coloured at each point, before any light falls on it. */
    public Surface surface() {
        return surface(new Seen().at(), new Seen().covers());
    }

    /** Its face where {@code at} is the point on the wall and {@code covers} how much of it a pixel covers there. */
    public Surface surface(final Point at, final Real covers) {
        final Jointing jointing = new Jointing(stone, joint);
        final Scatter grains = new Scatter(at.across().over(GRAINED).floored(), at.up().over(GRAINED).floored(),
                GRAIN, GRAINED);
        return new Surface(pale,
                List.of(new Term("stones", dark, jointing.stones(at).scatter(SHADE).settling(covers), stone / 2),
                        new Term("grain", grain, grains.some(GRAINS, covers).times(DARKENED), GRAINED),
                        new Term("mortar", mortar, jointing.share(at, covers, mortared), joint)),
                new Colour(0, 0, 0), 8);
    }
}

package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * A wall of bricks, or of anything laid as bricks are, worked out brick by brick: bricks of a size, laid in a bond
 * with a joint of mortar between, each brick somewhere between a paler and a darker colour, each flecked. Its face
 * is a surface of terms over the paler colour, each as fine as it is looked at:
 *
 * <ul>
 * <li>the firing, a brick's own share of the darker colour, the same all over the brick;</li>
 * <li>its flecks, each kind a few grains of every brick coloured;</li>
 * <li>the mortar, in the joints, over them all.</li>
 * </ul>
 *
 * <p>From further off the flecks go first, then the joints, then the difference between one brick and the next;
 * what is left is one colour, the wall's average, which Java works out.
 *
 * @param joint how thick the mortar is between bricks, in centimetres
 */
public record BrickWall(Brick brick, Bond bond, double joint, Colour pale, Colour dark, Colour mortar,
                        List<Fleck> flecks) {

    /** What tells the firing's scatter from the flecks', which are numbered on from it. */
    private static final int FIRING = 1;

    public BrickWall {
        flecks = List.copyOf(flecks);
    }

    /** Its face: how it is coloured at each point, before any light falls on it. Nothing here shines. */
    public Surface surface() {
        return surface(new Seen().at(), new Seen().covers());
    }

    /**
     * Its face where {@code at} is how far across the wall a point is and how far up from the wall's foot, and
     * {@code covers} how much of the face a pixel covers there: for a wall whose foot is not its frame's ground.
     */
    public Surface surface(final Point at, final Real covers) {
        final Laying laying = new Laying(brick, bond, joint);
        final List<Term> terms = new ArrayList<>();
        final Scatter firing = new Scatter(laying.brick(at), laying.course(at), FIRING, brick.height());
        terms.add(new Term("firing", dark, firing.settling(covers), brick.height()));
        for (int which = 0; which < flecks.size(); which++) {
            final Fleck fleck = flecks.get(which);
            final Scatter grains = new Scatter(at.across().over(fleck.across()).floored(),
                    at.up().over(fleck.up()).floored(), FIRING + 1 + which, fleck.smallest());
            terms.add(new Term(fleck.name(), fleck.colour(),
                    grains.some(fleck.share(), covers).times(fleck.strength()), fleck.smallest()));
        }
        terms.add(new Term("mortar", mortar, laying.mortar(at, covers), joint));
        return new Surface(pale, terms, new Colour(0, 0, 0), 8);
    }
}

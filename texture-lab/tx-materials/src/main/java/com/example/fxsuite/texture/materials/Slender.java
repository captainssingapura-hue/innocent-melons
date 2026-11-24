package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Term;

/**
 * The colour a tree's young wood is where it is slender, laid over whatever its old bark is: wholly that colour
 * where the wood is as thin as {@code thin}, none of it where it is as thick as {@code thick}, and between them
 * the one giving way to the other. A willow's twigs are golden so, and a plane's olive, while their trunks are
 * neither. It goes by how thick the wood is at a point, which a tree's mesh says as the third of where on its bark
 * the point is; on a thing that says none, there is none of it.
 *
 * @param colour the young wood's colour
 * @param thin   how thick the wood is, in centimetres from its middle, where it is wholly that colour
 * @param thick  how thick it is where none of that colour is left
 */
public record Slender(String colour, double thin, double thick) {

    /** The young wood as a term to lay over a bark. */
    public Term term() {
        final Point at = new Seen().at();
        final Real share = new Real(thick).less(at.along()).over(thick - thin).held(0, 1)
                .times(at.along().past(new Real(0)));
        return new Term("slender wood", new Colour(colour), share, thick);
    }
}

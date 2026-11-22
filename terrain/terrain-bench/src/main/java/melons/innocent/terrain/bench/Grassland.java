package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The land as the bench colours it, by how high it is, until the land says what covers it: meadow low down, the
 * grass drier up the slopes from {@code low} to {@code high}, and moor over the heights above that; mottled in
 * patches some twelve metres across, which go to their average further off. Dull: it shines hardly at all.
 *
 * @param low  how high the grass begins to dry, in centimetres
 * @param high how high it is driest, and the moor begins
 */
public record Grassland(double low, double high) {

    /** How big a patch of the mottling is, in centimetres; how much darker the darkest patch is. */
    private static final double PATCH = 1_200;
    private static final double MOTTLED = 0.12;
    /** How far over the driest grass the moor takes over, as a share of the slope's rise. */
    private static final double MOOR = 0.5;

    public Grassland {
        if (!(low < high)) {
            throw new IllegalArgumentException("the grass dries going up: from " + low + " to " + high);
        }
    }

    /** Its surface, worked out from how high on the land a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final double rise = high - low;
        final Real drier = at.up().less(new Real(low)).over(rise).held(0, 1);
        final Real moor = at.up().less(new Real(high)).over(rise * MOOR).held(0, 1);
        final Real patches = new Scatter(at.across().over(PATCH).floored(), at.along().over(PATCH).floored(), 3,
                PATCH).settling(seen.covers());
        return new Surface(new Colour("#5f8a3c"), List.of(
                new Term("drier grass up the slopes", new Colour("#9a9a55"), drier, PATCH),
                new Term("moor on the heights", new Colour("#7a6a52"), moor, PATCH),
                new Term("patches", new Colour("#3f5f2a"), patches.times(MOTTLED), PATCH)),
                new Colour(0.02, 0.02, 0.02), 4);
    }
}

package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A spot height of a topographic map: how high the ground is at one spot, marked where it tops out or bottoms out
 * between its contours, a summit or the deepest of a hollow.
 *
 * @param at     where
 * @param height how high the ground is there, in centimetres over the sea's level
 */
public record SpotHeight(Spot at, double height) implements Scalar {

    public SpotHeight {
        if (!Double.isFinite(height)) {
            throw new IllegalArgumentException("a spot height at some height, not " + height);
        }
    }
}

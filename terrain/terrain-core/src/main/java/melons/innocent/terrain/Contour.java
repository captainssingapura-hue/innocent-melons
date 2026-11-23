package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A contour of a topographic map: a loop the ground is at one height all the way round. Contours never cross, so
 * they nest: one inside a lower one goes up a hill, one inside a higher one down into a hollow, and one inside
 * another as high holds the ground between them level.
 *
 * @param height how high the ground is along it, in centimetres over the sea's level
 * @param loop   where it runs
 */
public record Contour(double height, Loop loop) implements Scalar {

    public Contour {
        if (!Double.isFinite(height)) {
            throw new IllegalArgumentException("a contour at some height, not " + height);
        }
    }
}

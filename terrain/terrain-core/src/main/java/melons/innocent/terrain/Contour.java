package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A contour of a topographic map: a loop the ground is at one height all the way round. Contours never cross, so
 * they nest: one inside a lower one goes up a hill, one inside a higher one down into a hollow, and one inside
 * another as high holds the ground between them level. One marked level holds all the ground inside it, but for what
 * lies inside the contours within it, at its height: a valley's floor, a terrace, a churchyard, a lake's bed.
 *
 * @param height how high the ground is along it, in centimetres over the sea's level
 * @param loop   where it runs
 * @param level  whether the ground inside it is level at its height
 */
public record Contour(double height, Loop loop, boolean level) implements Scalar {

    public Contour {
        if (!Double.isFinite(height)) {
            throw new IllegalArgumentException("a contour at some height, not " + height);
        }
    }

    /** A contour the ground goes on through, up or down. */
    public Contour(final double height, final Loop loop) {
        this(height, loop, false);
    }
}

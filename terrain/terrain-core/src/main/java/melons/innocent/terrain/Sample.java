package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * The ground at a spot, as already worked out: where the spot is, how high the ground is there, and how steep: how
 * far it rises for each unit across, the steepest way. Whoever has the ground's heights already, a sheet of them, says
 * so, and nothing asks the ground again.
 *
 * @param x      how far across
 * @param z      how far along
 * @param height how high the ground is there, in centimetres
 * @param slope  how steep it is there, the steepest way
 */
public record Sample(double x, double z, double height, double slope) implements Scalar {
}

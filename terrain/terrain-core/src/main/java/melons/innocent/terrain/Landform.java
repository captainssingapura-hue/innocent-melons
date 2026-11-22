package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A landform, laid over the ground laid before it: a plain levelling it, a mount or a ridge raised on it, a channel
 * cut into it, a coast sinking what lies seaward of it under the sea's level. Each is named, and shaped by curves;
 * one laid along the map's lines follows a path of them. What it makes of the ground is worked out by its shaping.
 */
public sealed interface Landform extends Scalar permits Plain, Mount, Ridge, Channel, Coast {

    /** Its name, as the map gives it. */
    String name();

    /** How it shapes the ground, laid along {@code map}'s lines where it follows a path of them. */
    Shaping shaping(Graph<Spot, Course> map);

    /** How a landform shapes the ground: from how high the ground under it is at a spot, how high it makes it. */
    @FunctionalInterface
    interface Shaping {

        /** How high the ground is at ({@code x}, {@code z}) with this laid over it, {@code under} high before. */
        double laid(double under, double x, double z);
    }
}

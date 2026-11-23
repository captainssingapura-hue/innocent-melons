package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;

/**
 * A land feature that is water: the sea, a river. Besides where it lies, it says how high its top is: at every spot
 * within its reach, whether the ground there is under it or not, so that whoever draws it can lay its top level
 * across the shore and let the ground rise out of it where it does. Its top lies level across, as water does.
 */
public sealed interface WaterBody extends Feature permits Sea, Watercourse {

    /** How high its top is, along {@code map}'s lines and over {@code ground}. */
    Level level(Graph<Spot, Course> map, Ground ground);

    /** How high a water's top is at a spot. */
    @FunctionalInterface
    interface Level {

        /** How high its top is at ({@code x}, {@code z}): not a number beyond its reach. */
        double at(double x, double z);
    }
}

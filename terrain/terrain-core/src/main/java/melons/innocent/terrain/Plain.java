package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;

/**
 * A plain: the ground made level at {@code level}, whatever lay there before. Laid first, it is the ground every
 * other landform is laid over.
 *
 * @param name  its name
 * @param level how high it lies, in centimetres over the sea's level
 */
public record Plain(String name, double level) implements Landform {

    @Override
    public Shaping shaping(final Graph<Spot, Course> map) {
        return (under, x, z) -> level;
    }
}

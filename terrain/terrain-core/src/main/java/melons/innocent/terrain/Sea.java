package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;

/**
 * The sea, or a bay of it: lying to the right of a {@code shore} of the map's lines wherever the ground is lower than
 * its {@code level}. Where the ground rises out of it, there is no sea, whichever side of the shore.
 *
 * @param label its label
 * @param kind  the kind of thing it is
 * @param hue   the colour it is shown in
 * @param shore the waterline, the sea on its right
 * @param level how high its top lies, in centimetres
 */
public record Sea(String label, Leaf<?> kind, Hue hue, Path shore, double level) implements Feature {

    @Override
    public Coverage coverage(final Terrain terrain) {
        final Traced traced = new Traced(shore, terrain.map());
        return at -> at.height() < level && traced.near(at.x(), at.z()).side() < 0;
    }
}

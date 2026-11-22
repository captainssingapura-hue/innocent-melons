package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;

/**
 * A feature lying wherever the ground is steeper than it says: bare rock where nothing holds on.
 *
 * @param label its label
 * @param kind  the kind of thing it is
 * @param hue   the colour it is shown in
 * @param rise  how far the ground rises for each unit across, at least, where it lies
 */
public record Steep(String label, Leaf<?> kind, Hue hue, double rise) implements Feature {

    @Override
    public Coverage coverage(final Terrain terrain) {
        return at -> at.slope() > rise;
    }
}

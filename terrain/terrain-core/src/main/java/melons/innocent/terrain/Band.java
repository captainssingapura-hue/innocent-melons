package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;

/**
 * A feature lying wherever the ground is as high as it says: from {@code low} up to, not including, {@code high}.
 * Meadow over the plain, scrub higher up the slopes, sand along the waterline.
 *
 * @param label its label
 * @param kind  the kind of thing it is
 * @param hue   the colour it is shown in
 * @param low   how high the ground is where it begins, in centimetres
 * @param high  how high the ground is where it ends, past its beginning
 */
public record Band(String label, Leaf<?> kind, Hue hue, double low, double high) implements Feature {

    public Band {
        if (!(low < high)) {
            throw new IllegalArgumentException(label + ": a band from lower ground to higher, not " + low + " to "
                    + high);
        }
    }

    @Override
    public Coverage coverage(final Terrain terrain) {
        return at -> at.height() >= low && at.height() < high;
    }
}

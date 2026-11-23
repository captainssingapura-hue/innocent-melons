package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.terrain.Sample;

import java.util.List;

/**
 * How the bench colours the ground: in layers, laid in order, the first everywhere and each later one over those
 * before it wherever it says it lies, each in its own surface; the ground's raw shape is drawn in the first while the
 * rest are worked out. Plainly, all in one material; or by the land's features, each in its colour.
 */
public interface Painting {

    /** The layers, in the order they are laid, each by its label and the colour the legend shows it in. */
    List<Shown> legend();

    /** The surface layer {@code layer} is drawn in, by its place in the legend. */
    Surface surface(int layer);

    /** Whether layer {@code layer} lies at {@code at}, the ground there as already worked out; the first, always. */
    boolean covers(int layer, Sample at);

    /**
     * How high the water's top is at {@code at}, where this painting draws water as a level sheet over the ground
     * rather than as a layer of it: not a number where no water reaches, or where it draws none so.
     */
    default double water(final Sample at) {
        return Double.NaN;
    }

    /** The layer whose surface water's level sheet is drawn in: none, less than nothing, where it draws none. */
    default int waterLayer() {
        return -1;
    }

    /** What lies at ({@code x}, {@code z}), by its label, the ground there worked out afresh. */
    String at(double x, double z);

    /**
     * A layer as the legend shows it.
     *
     * @param label what it says
     * @param red   how much red its swatch is, from nothing to one
     * @param green how much green
     * @param blue  how much blue
     */
    record Shown(String label, double red, double green, double blue) {
    }
}

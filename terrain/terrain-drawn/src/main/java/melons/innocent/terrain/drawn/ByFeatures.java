package melons.innocent.terrain.drawn;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.terrain.Feature;
import melons.innocent.terrain.Hue;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * The ground painted by the land's features: first bare ground, grey, everywhere, the raw shape of the land; then
 * each feature, in the order they are laid, in its colour, plain and dull, wherever it lies. A map's colours, for
 * telling the features apart, no material yet.
 *
 * @param features the land's features
 */
public record ByFeatures(LandFeatures features) implements Painting {

    /** What the legend says of the first layer, and its colour. */
    static final String BARE = "bare ground";
    private static final Hue GREY = new Hue("#9a9a90");
    /** How much of the sun's colour a layer's surface gives back as a highlight, and how tight it is. */
    private static final double DULL = 0.02;
    private static final double TIGHT = 4;

    @Override
    public List<Shown> legend() {
        final List<Shown> legend = new ArrayList<>(List.of(new Shown(BARE, GREY.red(), GREY.green(), GREY.blue())));
        for (final Feature feature : features.laid()) {
            legend.add(new Shown(feature.label(), feature.hue().red(), feature.hue().green(), feature.hue().blue()));
        }
        return List.copyOf(legend);
    }

    @Override
    public Surface surface(final int layer) {
        final Hue hue = layer == 0 ? GREY : features.laid().get(layer - 1).hue();
        return new Surface(new Colour(hue.red(), hue.green(), hue.blue()), List.of(), new Colour(DULL, DULL, DULL),
                TIGHT);
    }

    @Override
    public boolean covers(final int layer, final Sample at) {
        return layer == 0 || features.coverages().get(layer - 1).covers(at);
    }

    @Override
    public String at(final double x, final double z) {
        return features.at(x, z).map(Feature::label).orElse(BARE);
    }
}

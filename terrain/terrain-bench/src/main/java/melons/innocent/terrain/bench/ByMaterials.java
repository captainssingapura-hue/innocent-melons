package melons.innocent.terrain.bench;

import com.example.fxsuite.existence.Earthworks;
import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.terrain.Feature;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.WaterBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * The ground drawn in the lab's materials: first plain, uncoloured, everywhere, the land's shape; then each feature,
 * in the order they are laid, in the material its kind is made of, where the lab has one: the meadow in turf, the
 * beach in sand, the crags in bedrock, the fields in topsoil, the river and the bay in water. A feature whose kind the
 * lab has no material for yet is left plain. Which material a kind is made of is the drawing's to say, not the land's.
 * Water is no layer of the ground: it is drawn as a level sheet at its top, over the ground under it, which is drawn as
 * whatever else lies there.
 *
 * @param features the land's features
 */
public record ByMaterials(LandFeatures features) implements Painting {

    /** What the legend says of the first layer. */
    static final String PLAIN = "plain";
    /** How light the plain surface is, and how much of the sun's colour it gives back, how tightly. */
    private static final double LIGHT = 0.72;
    private static final double DULL = 0.02;
    private static final double TIGHT = 4;

    /** The material each kind of feature is made of, where the lab has one. */
    Map<Leaf<?>, Material> madeOf() {
        return Map.of(new Terrains.Meadow(), new Materials.Turf(), new Terrains.Sand(), new Materials.Sand(),
                new Terrains.Rock(), new Materials.Bedrock(), new Earthworks.Field(), new Materials.Topsoil(),
                new Features.River(), new Materials.Water(), new Features.Bay(), new Materials.Water(),
                new Features.Sea(), new Materials.Water(), new Features.Pond(), new Materials.Water(),
                new Features.Lake(), new Materials.Water());
    }

    /** The material layer {@code layer} is drawn in; none for the first, or for a feature the lab has none for. */
    Optional<Material> material(final int layer) {
        return layer == 0 ? Optional.empty()
                : Optional.ofNullable(madeOf().get(features.laid().get(layer - 1).kind()));
    }

    @Override
    public List<Shown> legend() {
        final List<Shown> legend = new ArrayList<>(List.of(new Shown(PLAIN, LIGHT, LIGHT, LIGHT)));
        for (int layer = 1; layer <= features.laid().size(); layer++) {
            final Feature feature = features.laid().get(layer - 1);
            final String in = material(layer).map(material -> material.title().toLowerCase(Locale.ROOT))
                    .orElse(PLAIN);
            legend.add(new Shown(feature.label() + ": " + in, feature.hue().red(), feature.hue().green(),
                    feature.hue().blue()));
        }
        return List.copyOf(legend);
    }

    @Override
    public Surface surface(final int layer) {
        return material(layer).map(material -> new Shades().of(material))
                .orElse(new Surface(new Colour(LIGHT, LIGHT, LIGHT), List.of(), new Colour(DULL, DULL, DULL), TIGHT));
    }

    /** Whether layer {@code layer} lies on the ground: the first, and every feature but water, which lies over it. */
    @Override
    public boolean covers(final int layer, final Sample at) {
        return layer == 0 || !(features.laid().get(layer - 1) instanceof WaterBody)
                && features.coverages().get(layer - 1).covers(at);
    }

    @Override
    public double water(final Sample at) {
        return features.water(at.x(), at.z());
    }

    /** The last water feature's layer: its material, water, the water's sheet is drawn in. */
    @Override
    public int waterLayer() {
        for (int layer = features.laid().size(); layer > 0; layer--) {
            if (features.laid().get(layer - 1) instanceof WaterBody) {
                return layer;
            }
        }
        return -1;
    }

    @Override
    public String at(final double x, final double z) {
        return features.at(x, z).map(Feature::label).orElse(PLAIN);
    }
}

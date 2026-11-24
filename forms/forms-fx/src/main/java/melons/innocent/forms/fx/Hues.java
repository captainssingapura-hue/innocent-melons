package melons.innocent.forms.fx;

import com.example.fxsuite.avatar.build.Material;
import javafx.scene.paint.Color;
import melons.innocent.forms.Facets;

/**
 * The colour each material is drawn in by JavaFX, as it looks from a little way off: a plain colour to a material,
 * no pattern, the light and the eye's angle doing the rest; or, painted or tinted, the colour it is painted or tinted.
 */
public record Hues() {

    /** The colour {@code coat} is drawn in: what it is painted or tinted, else its material's. */
    public Color of(final Facets.Coat coat) {
        return coat.tint().map(t -> Color.color(t.red(), t.green(), t.blue())).orElseGet(() -> of(coat.material()));
    }

    /** The colour {@code material} is drawn in. */
    public Color of(final Material material) {
        return switch (material) {
            case CONCRETE, PRECAST -> Color.web("#b8b5ad");
            case BRICK -> Color.web("#9c5a43");
            case STONE -> Color.web("#9a958a");
            case GLASS -> Color.web("#6f8fa3");
            case METAL -> Color.web("#3c3f44");
            case GRAVEL -> Color.web("#a39f96");
            case SLATE -> Color.web("#4b5058");
            case TILE -> Color.web("#a4553a");
            case COPPER -> Color.web("#5f9e8a");
            case GLAZED -> Color.web("#d6a83a");
            case GREEN_GLAZED -> Color.web("#4f8a4a");
            case GREY_TILE -> Color.web("#5d6066");
            case LACQUER -> Color.web("#8e2a22");
            case PAINTED -> Color.web("#c9b48a");
            case TIMBER -> Color.web("#5a4030");
            case PLASTER -> Color.web("#b4523e");
            case SANDSTONE -> Color.web("#c9a978");
            case LIMESTONE -> Color.web("#e3ddd0");
            case GRASS, TURF, SOD -> Color.web("#6e9a48");
            case WATER, POOL_TILE -> Color.web("#4f86a8");
            case LEAVES -> Color.web("#4c7a3a");
            case BARK -> Color.web("#5b4a3a");
            case MUD, EARTH -> Color.web("#8a6d4e");
            case RED_EARTH -> Color.web("#a0583e");
            case PALE_CLAY -> Color.web("#cdbb98");
            case RAMMED_EARTH -> Color.web("#b08f68");
            case THATCH, REED -> Color.web("#b59a5e");
            case ASPHALT -> Color.web("#3a3a3c");
        };
    }
}

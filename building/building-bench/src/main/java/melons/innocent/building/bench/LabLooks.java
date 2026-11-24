package melons.innocent.building.bench;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import javafx.scene.paint.Color;
import melons.innocent.forms.Facets;
import melons.innocent.forms.Tint;
import melons.innocent.forms.fx.Hues;

import java.util.List;
import java.util.Optional;

/**
 * What each material a building's solids are of is drawn in when looked into closely: the texture lab's look for it,
 * where the lab has one, concrete as concrete, glass as glass, metal as steel, paint as gloss paint, brick, stone,
 * earth; else a plain surface in the colour JavaFX draws it in. Painted, a plain surface in its paint, what is under
 * it hidden; glass tinted, the glass's look in its tint. The one place the solids' materials meet the lab's, so
 * when those give way to the lab's taxonomy of materials, only this changes.
 */
public final class LabLooks {

    private final Shades shades = new Shades();
    private final Hues hues = new Hues();

    /** The lab's material {@code material} is, if it has one. */
    public Optional<com.example.fxsuite.texture.materials.Material> lab(final Material material) {
        return Optional.ofNullable(switch (material) {
            case CONCRETE, PRECAST -> new Materials.Concrete();
            case GLASS -> new Materials.Glass();
            case METAL -> new Materials.Steel();
            case PAINTED, LACQUER -> new Materials.GlossPaint();
            case BRICK -> new Materials.CommonRedBrick();
            case STONE, SANDSTONE, LIMESTONE -> new Materials.DressedStone();
            case MUD, PALE_CLAY, RAMMED_EARTH, RED_EARTH -> new Materials.Adobe();
            case GRAVEL -> new Materials.Gravel();
            case GRASS, TURF, SOD -> new Materials.Turf();
            case WATER -> new Materials.Water();
            case EARTH -> new Materials.Topsoil();
            default -> null;
        });
    }

    /** The surface {@code coat} is drawn in: painted, a plain one in its paint; glass tinted, glass in its tint. */
    public Surface surface(final Facets.Coat coat) {
        return coat.tint().map(t -> coat.material() == Material.GLASS ? tinted(surface(coat.material()), t)
                : plain(Color.color(t.red(), t.green(), t.blue()))).orElseGet(() -> surface(coat.material()));
    }

    /** {@code glass} its own colour {@code tint}: as much let through, mirrored and glancing as it is. */
    private Surface tinted(final Surface glass, final Tint tint) {
        final Surface tinted = new Surface(new Colour(tint.red(), tint.green(), tint.blue()), glass.terms(),
                glass.shine(), glass.tightness()).mirroring(glass.mirror()).clear(glass.through());
        return glass.glance().map(tinted::glancing).orElse(tinted);
    }

    /** The surface {@code material} is drawn in: the lab's look, or a plain one in its colour. */
    public Surface surface(final Material material) {
        return lab(material).map(shades::of).orElseGet(() -> plain(hues.of(material)));
    }

    /** {@code colour} as a plain surface, a little shine. */
    private Surface plain(final Color colour) {
        return new Surface(new Colour(colour.getRed(), colour.getGreen(), colour.getBlue()), List.of(),
                new Colour(0.05, 0.05, 0.05), 8);
    }
}

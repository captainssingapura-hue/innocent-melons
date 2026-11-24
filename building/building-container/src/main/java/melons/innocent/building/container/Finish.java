package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Solid;
import melons.innocent.forms.Tint;

import java.util.Optional;

/**
 * What a container house's parts are finished in, as it is asked to be: its steel painted cream, charcoal or left to
 * rust; bare steel; titanium or gun metal; or clad in timber, concrete, brick, stone or white plaster.
 */
public enum Finish {
    CREAM(Material.METAL, "#d8cba8"),
    CHARCOAL(Material.METAL, "#34373b"),
    RUST(Material.METAL, "#8b4a2b"),
    STEEL(Material.METAL, null),
    TITANIUM(Material.TITANIUM, null),
    GUN_METAL(Material.GUN_METAL, null),
    TIMBER(Material.TIMBER, null),
    CONCRETE(Material.CONCRETE, null),
    BRICK(Material.BRICK, null),
    STONE(Material.STONE, null),
    PLASTER(Material.PLASTER, "#ece8df");

    private final Material material;
    private final String paint;

    Finish(final Material material, final String paint) {
        this.material = material;
        this.paint = paint;
    }

    /** What it is of. */
    public Material material() {
        return material;
    }

    /** What it is painted, if it is. */
    public Optional<Tint> paint() {
        return Optional.ofNullable(paint).map(Tint::new);
    }

    /** {@code solid}, of its material, painted as it is. */
    Solid on(final Solid solid) {
        return paint().map(solid::tinted).orElse(solid);
    }
}

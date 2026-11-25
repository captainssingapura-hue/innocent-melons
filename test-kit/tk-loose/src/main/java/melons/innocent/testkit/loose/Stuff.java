package melons.innocent.testkit.loose;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;

import java.util.function.Supplier;

/**
 * What a loose body on the test ground is made of: one of the lab's materials, which says how it looks, and how
 * dense that material is, which with the body's shape says what it weighs. The densities are general figures, kept
 * here until the lab keeps one with each of its leaves.
 */
public enum Stuff {

    /** Timber: a crate. */
    TIMBER(600, Materials.Timber::new),
    /** Cast concrete: a pillar. */
    CONCRETE(2_400, Materials.Concrete::new),
    /** Steel: a beam. */
    STEEL(7_850, Materials.Steel::new),
    /** A watermelon, rind and all. */
    RIND(960, Materials.Rind::new),
    /** Rubber: a ball. */
    RUBBER(1_100, Materials.Rubber::new);

    private final double dense;
    private final Supplier<Material> material;

    Stuff(final double dense, final Supplier<Material> material) {
        this.dense = dense;
        this.material = material;
    }

    /** How many kilograms a cubic metre of it weighs. */
    public double dense() {
        return dense;
    }

    /** The lab's material it is. */
    public Material material() {
        return material.get();
    }
}

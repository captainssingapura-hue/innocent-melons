package melons.innocent.testkit.loose;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.function.Supplier;

/**
 * What a loose body on the test ground is made of: one of the lab's materials, which says how it looks, and where
 * that material takes its colour from the thing, the colour; how dense it is, which with the body's shape says what
 * it weighs; how hard a blow breaks a thing of it that is struck, in joules; and how fast a thing of it must come
 * down to break itself, in metres a second. The densities are general figures and the strengths set by trying, kept
 * here until the lab keeps them with each of its leaves.
 */
public enum Stuff {

    /** Timber: a crate. */
    TIMBER(600, 400, 15, 0xffffff, Materials.Timber::new),
    /** Cast concrete: a pillar. */
    CONCRETE(2_400, 20_000, 25, 0xffffff, Materials.Concrete::new),
    /** Steel: a beam. No blow here breaks it. */
    STEEL(7_850, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0xffffff, Materials.Steel::new),
    /** A watermelon, rind and all. */
    RIND(960, 300, 11, 0xffffff, Materials.Rind::new),
    /** Rubber: a ball. No blow breaks it. */
    RUBBER(1_100, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0xffffff, Materials.Rubber::new),
    /** A watermelon's flesh, as a slice shows it: the lab has no flesh yet, so gloss paint in its red. */
    FLESH(960, 60, 8, 0xe2364a, Materials.GlossPaint::new),
    /** An apple, peel and all: gloss paint in an apple's red. */
    PEEL(800, 60, 10, 0xb5121b, Materials.GlossPaint::new);

    private final double dense;
    private final double breaks;
    private final double bursts;
    private final int colour;
    private final Supplier<Material> material;

    Stuff(final double dense, final double breaks, final double bursts, final int colour,
          final Supplier<Material> material) {
        this.dense = dense;
        this.breaks = breaks;
        this.bursts = bursts;
        this.colour = colour;
        this.material = material;
    }

    /** How many kilograms a cubic metre of it weighs. */
    public double dense() {
        return dense;
    }

    /** How many joules a blow must bring to break a thing of it that it strikes. */
    public double breaks() {
        return breaks;
    }

    /** How many metres a second a thing of it must come down at to break itself. */
    public double bursts() {
        return bursts;
    }

    /** The colour a thing of it is told, for a material that takes its colour from the thing: white, of most. */
    public Vec3 tint() {
        return new Vec3((colour >> 16 & 0xff) / 255.0, (colour >> 8 & 0xff) / 255.0, (colour & 0xff) / 255.0);
    }

    /** The lab's material it is. */
    public Material material() {
        return material.get();
    }
}

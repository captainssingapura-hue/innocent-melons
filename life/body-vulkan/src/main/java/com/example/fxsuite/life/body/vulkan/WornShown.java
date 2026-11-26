package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * What the body bench has it wear ({@link com.example.fxsuite.life.body.wear.Armour},
 * {@link com.example.fxsuite.life.body.head.Helmet}, {@link com.example.fxsuite.life.body.wear.Tailor}): which kinds
 * of thing worn are on, of those {@link #kinds}; what the armour is made of, one of those {@link #offered}; its
 * colour, which a painted piece takes and a bare metal's own keeps; its visor's colour, a dark glass under a lacquer;
 * and its cloth's colour.
 *
 * @param on       which kinds are worn
 * @param material what the armour is made of
 * @param colour   its colour, if painted, as much red, green and blue as it has
 * @param visor    its visor's colour
 * @param cloth    its cloth's colour
 */
public record WornShown(List<Leaf<?>> on, Material material, Vec3 colour, Vec3 visor, Vec3 cloth) {

    public WornShown {
        on = List.copyOf(on);
    }

    /** The kinds it may wear, in the order they are offered. */
    public List<Leaf<?>> kinds() {
        return List.of(new Wearables.Helmet(), new Wearables.Gorget(), new Wearables.Cuirass(),
                new Wearables.Pauldron(), new Wearables.Vambrace(), new Wearables.Gauntlet(), new Wearables.Fauld(),
                new Wearables.Greave(), new Wearables.Tights(), new Wearables.Briefs());
    }

    /** Whether it wears {@code kind}. */
    public boolean wears(final Leaf<?> kind) {
        return on.contains(kind);
    }

    /** What the armour may be made of: bare metals, each its own colour, and paints, each the colour told. */
    public List<Material> offered() {
        return List.of(new Materials.Steel(), new Materials.Titanium(), new Materials.Gunmetal(),
                new Materials.Gold(), new Materials.Bronze(), new Materials.MetallicPaint(),
                new Materials.GlossPaint());
    }

    /** What a visor is made of: a lacquer over the colour told, mirroring the sky. */
    public Material glass() {
        return new Materials.GlossPaint();
    }

    /**
     * What its cloth is drawn as: matte, taking the colour told, as skin is drawn; till there are materials of cloth
     * to draw it in.
     */
    public Material fabric() {
        return new Materials.Skin();
    }
}

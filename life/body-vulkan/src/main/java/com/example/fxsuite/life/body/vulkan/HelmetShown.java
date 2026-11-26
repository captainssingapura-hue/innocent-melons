package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * The helmet as the body bench has it on show ({@link com.example.fxsuite.life.body.head.Helmet}): whether it is
 * worn; what its shell is made of, one of those {@link #offered}; its colour, which a painted shell takes and a bare
 * metal's own keeps; and its visor's colour, a dark glass under a lacquer.
 *
 * @param on       whether it is worn
 * @param material what its shell is made of
 * @param colour   its shell's colour, if painted, as much red, green and blue as it has
 * @param visor    its visor's colour
 */
public record HelmetShown(boolean on, Material material, Vec3 colour, Vec3 visor) {

    /** What a shell may be made of: bare metals, each its own colour, and paints, each the colour told. */
    public List<Material> offered() {
        return List.of(new Materials.Steel(), new Materials.Titanium(), new Materials.Gunmetal(),
                new Materials.Gold(), new Materials.Bronze(), new Materials.MetallicPaint(),
                new Materials.GlossPaint());
    }

    /** What a visor is made of: a lacquer over the colour told, mirroring the sky. */
    public Material glass() {
        return new Materials.GlossPaint();
    }
}

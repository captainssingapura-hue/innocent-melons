package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * What the body bench has it wear ({@link com.example.fxsuite.life.body.wear.Armour},
 * {@link com.example.fxsuite.life.body.head.Helmet}, {@link com.example.fxsuite.life.body.wear.Tailor},
 * {@link com.example.fxsuite.life.body.wear.Boots}): which kinds of thing worn are on, of those {@link #kinds}; what
 * the armour is made of, one of those {@link #offered}; its colour, which a painted piece takes and a bare metal's own
 * keeps; its visor's colour, a dark glass under a lacquer; and its cloth's colour. Its boots' leather a lacquered
 * dark brown, their soles rubber.
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
                new Wearables.Pauldron(), new Wearables.Couter(), new Wearables.Vambrace(), new Wearables.Gauntlet(),
                new Wearables.Fauld(), new Wearables.Cuisse(), new Wearables.Poleyn(), new Wearables.Greave(),
                new Wearables.Sabaton(), new Wearables.Boot(), new Wearables.Tights(), new Wearables.Briefs());
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

    /** Everything a piece worn may be drawn in: what the armour may be made of, and a boot's sole's rubber. */
    public List<Material> drawnIn() {
        final List<Material> all = new java.util.ArrayList<>(offered());
        all.add(new Materials.Rubber());
        return all;
    }

    /** What a visor is made of: a lacquer over the colour told, mirroring the sky. */
    public Material glass() {
        return new Materials.GlossPaint();
    }

    /** What a hard piece in {@code coat} is drawn in. */
    public Material material(final BodyPieces.Coat coat) {
        return switch (coat) {
            case ARMOUR -> material;
            case GLASS, LEATHER -> new Materials.GlossPaint();
            case SOLE -> new Materials.Rubber();
        };
    }

    /** The colour a hard piece in {@code coat} is drawn in. */
    public Vec3 colour(final BodyPieces.Coat coat) {
        return switch (coat) {
            case ARMOUR -> colour;
            case GLASS -> visor;
            case LEATHER -> web(Paint.BOOT.colour());
            case SOLE -> web(Paint.SOLE.colour());
        };
    }

    /**
     * What its cloth is drawn as: matte, taking the colour told, as skin is drawn; till there are materials of cloth
     * to draw it in.
     */
    public Material fabric() {
        return new Materials.Skin();
    }

    /** The web colour {@code hex}, as much red, green and blue as it has. */
    private Vec3 web(final String hex) {
        final int rgb = Integer.parseInt(hex.substring(1), 16);
        return new Vec3((rgb >> 16 & 0xff) / 255.0, (rgb >> 8 & 0xff) / 255.0, (rgb & 0xff) / 255.0);
    }
}

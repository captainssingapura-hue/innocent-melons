package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * What each of a figure's pieces is drawn in, by what it is of ({@link Paint}): its shell, its panels, its visor, its
 * frame, its housings, its rods; each a coat, a material and the colour it is painted, which only a paint takes, a
 * bare metal, enamel, rubber or lit glass keeping its own. As built, each as the body shop paints a robot: its frame
 * steel, its housings gunmetal, its shell and its panels a gloss of their colours, its visor and its core lit glass.
 *
 * @param coats what each is drawn in, where it is not as built
 */
public record Paintwork(Map<Paint, Coat> coats) {

    /** A coat: {@code material}, painted {@code colour} if it is a paint, as much red, green and blue as it has. */
    public record Coat(Material material, Vec3 colour) {

        /** Whether its colour is its own to choose: whether it is a paint. */
        public boolean painted() {
            return material instanceof Materials.GlossPaint || material instanceof Materials.MetallicPaint;
        }
    }

    public Paintwork {
        final Map<Paint, Coat> c = new EnumMap<>(Paint.class);
        c.putAll(coats);
        coats = java.util.Collections.unmodifiableMap(c);
    }

    /** Each as built. */
    public Paintwork() {
        this(Map.of());
    }

    /** What the pieces of {@code paint} are drawn in. */
    public Coat of(final Paint paint) {
        final Coat changed = coats.get(paint);
        return changed != null ? changed : new Coat(built(paint), colour(paint.colour()));
    }

    /** This paintwork with the pieces of {@code paint} drawn in {@code coat}. */
    public Paintwork with(final Paint paint, final Coat coat) {
        final Map<Paint, Coat> c = new EnumMap<>(Paint.class);
        c.putAll(coats);
        c.put(paint, coat);
        return new Paintwork(c);
    }

    /**
     * What any piece may be drawn in: the paints, which take the colour told, a gloss and a metallic; the bare metals,
     * enamel and rubber, each its own colour; and lit glass, which glows.
     */
    public List<Material> offered() {
        return List.of(new Materials.GlossPaint(), new Materials.MetallicPaint(), new Materials.Steel(),
                new Materials.Titanium(), new Materials.Aluminium(), new Materials.Gunmetal(), new Materials.Gold(),
                new Materials.Bronze(), new Materials.Enamel(), new Materials.Rubber(), new Materials.LitGlass());
    }

    /**
     * What {@code paint} is drawn in as built: lit glass if it glows; a bare metal, gunmetal for a housing and steel
     * for the rest, if it is a metal; a gloss of its colour else.
     */
    private Material built(final Paint paint) {
        return switch (paint.finish()) {
            case GLOW -> new Materials.LitGlass();
            case METAL -> paint == Paint.HOUSING ? new Materials.Gunmetal() : new Materials.Steel();
            case GLOSS, MATTE -> paint == Paint.SOLE ? new Materials.Rubber() : new Materials.GlossPaint();
        };
    }

    /** The web colour {@code hex}, as much red, green and blue as it has. */
    private Vec3 colour(final String hex) {
        final int rgb = Integer.parseInt(hex.substring(1), 16);
        return new Vec3((rgb >> 16 & 0xff) / 255.0, (rgb >> 8 & 0xff) / 255.0, (rgb & 0xff) / 255.0);
    }
}

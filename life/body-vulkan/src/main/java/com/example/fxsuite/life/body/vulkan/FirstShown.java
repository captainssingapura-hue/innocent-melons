package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BodyGroup;
import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * What the body bench has on show to begin with, as the run was told: each group as {@code -Dbody.<group>} says, else
 * as skin; the skin's colour, {@code -Dbody.colour}; the helmet as {@link #helmet} says.
 */
public record FirstShown() {

    /** What is on show to begin with. */
    public BodyShown of() {
        final Map<BodyGroup, Layer> layers = new EnumMap<>(BodyGroup.class);
        for (final BodyGroup g : BodyGroup.values()) {
            final String s = System.getProperty("body." + g.name().toLowerCase(Locale.ROOT), "")
                    .toLowerCase(Locale.ROOT);
            for (final Layer l : Layer.values()) {
                if (!s.isEmpty() && l.name().toLowerCase(Locale.ROOT).startsWith(s)) {
                    layers.put(g, l);
                }
            }
        }
        return new BodyShown(layers, colour("body.colour", Skins.COLOUR), helmet());
    }

    /**
     * The helmet as it is on show to begin with: worn unless {@code -Dbody.helmet=false}; its shell of what
     * {@code -Dbody.helmet.material} names (steel); its colour {@code -Dbody.helmet.colour}, its visor's
     * {@code -Dbody.visor.colour}.
     */
    private HelmetShown helmet() {
        final HelmetShown plain = new HelmetShown(!"false".equals(System.getProperty("body.helmet")),
                new Materials.Steel(), colour("body.helmet.colour", "#9aa3ad"), colour("body.visor.colour", "#0d1116"));
        final String named = System.getProperty("body.helmet.material", "").toLowerCase(Locale.ROOT);
        for (final Material m : plain.offered()) {
            if (!named.isEmpty() && m.getClass().getSimpleName().toLowerCase(Locale.ROOT).startsWith(named)) {
                return new HelmetShown(plain.on(), m, plain.colour(), plain.visor());
            }
        }
        return plain;
    }

    /** The colour the run was told as {@code name}, as much red, green and blue as it has; else {@code otherwise}. */
    private Vec3 colour(final String name, final String otherwise) {
        final javafx.scene.paint.Color c = javafx.scene.paint.Color.web(System.getProperty(name, otherwise));
        return new Vec3(c.getRed(), c.getGreen(), c.getBlue());
    }
}

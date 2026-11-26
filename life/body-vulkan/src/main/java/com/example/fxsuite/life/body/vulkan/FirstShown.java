package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BodyGroup;
import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * What the body bench has on show to begin with, as the run was told: each group as {@code -Dbody.<group>} says, else
 * as skin; the skin's colour, {@code -Dbody.colour}; what it wears as {@link #worn} says.
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
        return new BodyShown(layers, colour("body.colour", Skins.COLOUR), worn());
    }

    /**
     * What it wears to begin with: the kinds {@code -Dbody.wear} names, by their names, commas between (every piece
     * of armour and the tights and briefs; {@code none}, nothing); the armour of what {@code -Dbody.armour.material}
     * names (steel); its colour {@code -Dbody.armour.colour}, its visor's {@code -Dbody.visor.colour}; the cloth's
     * {@code -Dbody.cloth.colour}.
     */
    private WornShown worn() {
        final WornShown plain = new WornShown(List.of(), new Materials.Steel(), colour("body.armour.colour", "#9aa3ad"),
                colour("body.visor.colour", "#0d1116"), colour("body.cloth.colour", "#22262e"));
        final List<String> named = Arrays.stream(System.getProperty("body.wear", "helmet,gorget,cuirass,pauldron,"
                + "vambrace,gauntlet,fauld,greave,tights,briefs").toLowerCase(Locale.ROOT).split(","))
                .map(String::trim).toList();
        final List<Leaf<?>> on = plain.kinds().stream().filter(k -> named.contains(k.name())).toList();
        final String made = System.getProperty("body.armour.material", "").toLowerCase(Locale.ROOT);
        Material m = plain.material();
        for (final Material offered : plain.offered()) {
            if (!made.isEmpty() && offered.getClass().getSimpleName().toLowerCase(Locale.ROOT).startsWith(made)) {
                m = offered;
            }
        }
        return new WornShown(on, m, plain.colour(), plain.visor(), plain.cloth());
    }

    /** The colour the run was told as {@code name}, as much red, green and blue as it has; else {@code otherwise}. */
    private Vec3 colour(final String name, final String otherwise) {
        final javafx.scene.paint.Color c = javafx.scene.paint.Color.web(System.getProperty(name, otherwise));
        return new Vec3(c.getRed(), c.getGreen(), c.getBlue());
    }
}

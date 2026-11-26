package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BodyGroup;
import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.EnumMap;
import java.util.Map;

/**
 * What the body bench has on show: how deep each group of the body's parts is drawn, the colour of the skin, as much
 * red, green and blue as it has, and what it wears. Said by the JavaFX window on the bench's bus, taken up by the
 * card's thread as its next frame begins; whole, and never changed once said.
 *
 * @param layers how deep each group is drawn; a group not named, as skin
 * @param skin   the skin's colour
 * @param worn   what it wears, and how
 */
public record BodyShown(Map<BodyGroup, Layer> layers, Vec3 skin, WornShown worn) {

    public BodyShown {
        final Map<BodyGroup, Layer> all = new EnumMap<>(BodyGroup.class);
        for (final BodyGroup g : BodyGroup.values()) {
            all.put(g, layers.getOrDefault(g, Layer.SKIN));
        }
        layers = Map.copyOf(all);
    }

    /** How deep {@code g} is drawn. */
    public Layer of(final BodyGroup g) {
        return layers.get(g);
    }
}

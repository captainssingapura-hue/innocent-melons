package com.example.fxsuite.texture.materials;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Surface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Whatever is in the tree of materials is already shaded: every leaf has a surface, and a look can be written from
 * it; what the tree says mirrors, mirrors, and nothing else does; and what it says gives light, gives light, and
 * nothing else does.
 */
class ShadesTest {

    private final Shades shades = new Shades();

    @Test
    void everyLeafOfTheTreeHasASurfaceAndALookIsWrittenFromIt() {
        assertEquals(49, new Materials().tree().leaves().size());
        for (final Leaf<?> leaf : new Materials().tree().leaves()) {
            final Surface surface = shades.of((Material) leaf);
            final Look look = new Look(new Laid(), surface);
            assertTrue(look.fragment().code().contains("shown = vec4("), leaf.title() + " has its shader");
        }
    }

    @Test
    void whatTheTreeSaysMirrorsMirrorsAndWhatItSaysGivesLightGivesLight() {
        for (final Leaf<?> leaf : new Materials().tree().leaves()) {
            final Surface surface = shades.of((Material) leaf);
            assertEquals(leaf.answer(Mirroring.class).isPresent(), !dark(surface.mirror().node()),
                    leaf.title() + " mirrors, or does not, as its branch says");
            assertEquals(leaf.answer(Glowing.class).isPresent(), !dark(surface.glow().node()),
                    leaf.title() + " gives light, or does not, as its branch says");
        }
        assertFalse(dark(shades.of(new Materials.Steel()).mirror().node()));
    }

    /** Whether a colour is nothing at all: no red, green or blue. */
    private boolean dark(final com.example.fxsuite.vulkan.term.Node colour) {
        return colour instanceof Constant constant && constant.numbers().stream().allMatch(part -> part == 0);
    }
}

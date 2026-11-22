package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;

import java.util.List;

/**
 * The clear skin over the front of the eye: seen through almost wholly, so the iris behind it shows as it is, and
 * wet, so it carries a small bright glint of the sun and mirrors more of what is about it the more nearly it is
 * seen edge on. It is what makes an eye look alive.
 *
 * <p>It only lets through what is behind it: it does not bend it, as a real cornea does, and it casts no shadow.
 */
public record ClearCornea() {

    private static final double THROUGH = 0.94;

    /** Its surface: colourless, seen through, under a glint and a mirror that grows toward its edge. */
    public Surface surface() {
        return new Surface(new Colour("#ffffff"), List.of(), new Colour("#ffffff"), 500)
                .mirroring(new Colour(0.04, 0.04, 0.04)).glancing(new Colour(0.85, 0.87, 0.9)).clear(THROUGH);
    }
}

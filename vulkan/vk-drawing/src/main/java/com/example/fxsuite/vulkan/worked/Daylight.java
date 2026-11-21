package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * What a picture is lit by and what is behind everything in it: the light, and the sky's colour (as much red, green
 * and blue as it has).
 */
public record Daylight(Light light, Vec3 sky) {

    private static final double BRIGHTEST = 255;

    /**
     * A bench's day, as the engine's bench kit has it for its own view: a pale sky, a soft sun from above and a
     * little to one side, and a grey light over all.
     */
    public Daylight() {
        this(new Light(new Vec3(0.45, -1, 0.35),
                        new Vec3(0x9a / BRIGHTEST, 0x97 / BRIGHTEST, 0x8f / BRIGHTEST),
                        new Vec3(0x8e / BRIGHTEST, 0x95 / BRIGHTEST, 0x9c / BRIGHTEST)),
                new Vec3(0xc9 / BRIGHTEST, 0xdb / BRIGHTEST, 0xe8 / BRIGHTEST));
    }
}

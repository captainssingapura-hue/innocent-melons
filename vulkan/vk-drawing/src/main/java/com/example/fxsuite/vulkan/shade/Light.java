package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The light a frame is lit by: the way the sun shines (the way its light travels, in the world's frame), the sun's
 * light, and the light that falls on everything alike; each light as much red, green and blue as it has.
 */
public record Light(Vec3 sunTowards, Vec3 sun, Vec3 ambient) {
}

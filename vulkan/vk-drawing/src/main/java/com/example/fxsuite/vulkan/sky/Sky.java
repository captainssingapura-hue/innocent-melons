package com.example.fxsuite.vulkan.sky;

/**
 * The sky over a place at a moment: where the sun stands, where the moon stands, and how much of the moon's face
 * is lit, from nothing at new moon to 1 at full.
 */
public record Sky(Stands sun, Stands moon, double moonLit) {
}

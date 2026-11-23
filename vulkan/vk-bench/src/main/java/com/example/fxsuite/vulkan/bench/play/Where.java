package com.example.fxsuite.vulkan.bench.play;

/**
 * Where the avatar stood after a step, as the simulation has it, in centimetres: what a kept run checks a replay
 * against now and then. Two are the same only if every number is the same to the bit.
 */
public record Where(double x, double y, double z) {
}

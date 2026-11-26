package com.example.fxsuite.vulkan.bench.subject;

/**
 * How far each of a figure's feet is raised to stand on ground higher than the other's: the leg on the higher
 * ground folded at its hip and its knee by as much, its foot staying under where it was. In the figure's own
 * centimetres, never less than nothing; nothing for both on level ground.
 *
 * @param left  how far the left foot is raised
 * @param right how far the right foot is raised
 */
public record Uneven(double left, double right) {

    /** How little a foot may be raised and the ground still count as level under both. */
    private static final double HARDLY = 0.01;

    /** Both feet on level ground. */
    public Uneven() {
        this(0, 0);
    }

    /** Whether the ground is as good as level under both. */
    public boolean level() {
        return left < HARDLY && right < HARDLY;
    }
}

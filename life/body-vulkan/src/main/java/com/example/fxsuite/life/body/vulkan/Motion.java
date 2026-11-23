package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.Move;

/**
 * How the body bench's body is asked to move, as the JavaFX window says it on the bench's bus: how it goes (walking
 * and running, or a march), how fast, whether it is going or stopped, where in its stride it is stopped, how slowly
 * time goes for it, and whether nothing is being dragged, so a stopped body may be posed whole. Whole, never changed.
 *
 * @param move    how it goes
 * @param speed   how fast, cm/s, walking and running
 * @param playing whether it is going
 * @param at      where in its stride it is stopped, 0 to 1
 * @param slow    how slowly time goes for it, 1 as it is
 * @param settled whether nothing is being dragged
 */
public record Motion(Move move, double speed, boolean playing, double at, double slow, boolean settled) {

    /**
     * Where a stopped body stands: how it goes, how fast, where in its stride. Its skin and muscles are posed for one.
     *
     * @param move  how it goes
     * @param speed how fast, cm/s
     * @param at    where in its stride
     */
    public record Still(Move move, double speed, double at) {
    }

    /** Where it stands, stopped. */
    public Still still() {
        return new Still(move, speed, at);
    }

    /** The same, {@code at} that point of its stride: the card's thread saying where the going body has got to. */
    public Motion at(final double cycle) {
        return new Motion(move, speed, playing, cycle, slow, settled);
    }
}

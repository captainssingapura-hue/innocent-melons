package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Fruit;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A house that is half a giant watermelon, set down on its cut: its middle on the ground at {@code stands}, so many
 * {@code times} the size of a melon, its length turned {@code turned} degrees about the upright. It is solid: it
 * has no door and nothing inside.
 *
 * <p>What is in the way of whoever walks or flies there is made from the same sizes. The engine's simulation knows
 * boxes, not domes, so the dome is so many boxes one on another, each smaller than the one below, as a stepped
 * roof. Whoever lands on it stands on a step, a little above the rind or a little within it.
 */
public record MelonHouse(Vec3 stands, double times, double turned) {

    /** How much of the dome's width a box fills, at the height the box begins: its corners would else stand out. */
    private static final double WITHIN = 0.82;
    /** Where each box begins and ends, as shares of the dome's height. */
    private static final double FIRST = 0.45;
    private static final double SECOND = 0.75;
    private static final double THIRD = 0.93;

    /** What takes half a melon, in its own frame, to where the house stands: made as big, turned, and set down. */
    public Mat4 placed() {
        return new Mat4(stands).times(new Turned(new Vec3(0, 1, 0), turned).matrix())
                .times(new Sized(times).matrix());
    }

    /** How high it stands. */
    public double high() {
        return times * Fruit.WATERMELON.height() / 2;
    }

    /** The house as what is in the way: four boxes, one on another. */
    public List<Block> inTheWay() {
        final List<Block> steps = new ArrayList<>();
        final double[] shares = {0, FIRST, SECOND, THIRD, 1};
        for (int step = 0; step + 1 < shares.length; step++) {
            // as wide as the dome is where the box begins
            final double wide = WITHIN * Math.sqrt(1 - shares[step] * shares[step]);
            steps.add(new Block(stands.x(), stands.z(), wide * times * Fruit.WATERMELON.length() / 2,
                    wide * times * Fruit.WATERMELON.width() / 2, turned, shares[step] * high(),
                    shares[step + 1] * high()));
        }
        return steps;
    }
}

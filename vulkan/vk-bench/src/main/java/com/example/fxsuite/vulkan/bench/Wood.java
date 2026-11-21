package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Fruit;
import com.example.fxsuite.avatar.sim.Num;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A wood of giant watermelons on flat ground: so many squares {@code across} each way, each square {@code apart}
 * centimetres wide, most of them with one melon somewhere in it, ten to twenty times as big as a melon is, lying
 * turned any way. The square in the middle is left clear, to start in. The same wood every time: where each melon
 * lies comes from the engine's own scatter, which repeats.
 *
 * <p>Each melon is in the way of whoever walks there, as a block the engine's simulation knows: a box a little
 * within the melon's length and width, as tall as the melon.
 */
public record Wood(int across, double apart) {

    /** How many of the squares have a melon, of all of them; and how far from its square's middle one may lie. */
    private static final double FULL = 0.7;
    private static final double ASTRAY = 0.3;
    private static final double LEAST = 10;
    private static final double MOST = 20;
    /** How much of a melon's length and width is in the way: its ends and sides curve under, and are walked past. */
    private static final double SOLID = 0.8;

    /** One of the melons: where on the ground its middle is, how many times a melon's size it is, and how turned. */
    public record Giant(double x, double z, double times, double yaw) {
    }

    /** Every melon of the wood. */
    public List<Giant> giants() {
        final List<Giant> all = new ArrayList<>();
        final int half = across / 2;
        for (int row = -half; row <= half; row++) {
            for (int column = -half; column <= half; column++) {
                final boolean clear = row == 0 && column == 0;
                if (!clear && Num.hash(row, column, 1) < FULL) {
                    all.add(new Giant((column + (Num.hash(row, column, 2) - 0.5) * 2 * ASTRAY) * apart,
                            (row + (Num.hash(row, column, 3) - 0.5) * 2 * ASTRAY) * apart,
                            LEAST + (MOST - LEAST) * Num.hash(row, column, 4), 360 * Num.hash(row, column, 5)));
                }
            }
        }
        return all;
    }

    /**
     * What takes a melon, in its own frame, to where {@code giant} lies in the world: made as big, turned, and set
     * down with its underside on the ground. {@code rests} is how high a melon's middle is when it lies.
     */
    public Mat4 stands(final Giant giant, final double rests) {
        return new Mat4(new Vec3(giant.x(), giant.times() * rests, giant.z()))
                .times(new Turned(new Vec3(0, 1, 0), giant.yaw()).matrix())
                .times(new Sized(giant.times()).matrix());
    }

    /** The melons as what is in the way, for the engine's simulation. */
    public Obstacles obstacles() {
        final Fruit melon = Fruit.WATERMELON;
        final List<Block> blocks = new ArrayList<>();
        for (final Giant giant : giants()) {
            blocks.add(new Block(giant.x(), giant.z(), SOLID * giant.times() * melon.length() / 2,
                    SOLID * giant.times() * melon.width() / 2, giant.yaw(), 0, giant.times() * melon.height()));
        }
        return new Obstacles(blocks);
    }
}

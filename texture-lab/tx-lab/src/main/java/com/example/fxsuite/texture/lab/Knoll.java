package com.example.fxsuite.texture.lab;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A piece of rolling ground to look at what covers the land on: a knoll on a level slab, eight metres by six, a
 * little over a metre and a half high at its top. One side of it falls away gently and the other steeply, so a
 * cover is seen lying level, on a slope a person would walk up, and on a bank they would not. It stands on the
 * ground on a low plinth of itself, whose upright sides show the cover edge on.
 *
 * <p>In its own frame its middle is on the ground under the slab's middle; across is along the bench's row.
 */
public record Knoll(double across, double deep, double high) {

    /** How high the slab's level top is, and how many pieces the ground is cut in each way. */
    private static final double SLAB = 12;
    private static final int LONG = 80;
    private static final int WIDE = 60;
    /** How far from its top the knoll reaches: gently one way, steeply the other, and to each side. */
    private static final double GENTLE = 0.52;
    private static final double STEEP = 0.2;
    private static final double BESIDE = 0.42;

    /** The knoll the bench has. */
    public Knoll() {
        this(800, 600, 160);
    }

    /** How high the ground is at a place, from the knoll's own middle. */
    public double height(final double x, final double z) {
        final double reach = across * (x < 0 ? GENTLE : STEEP);
        final double out = (x / reach) * (x / reach) + (z / (deep * BESIDE)) * (z / (deep * BESIDE));
        return SLAB + (out < 1 ? high * (1 - out) * (1 - out) : 0);
    }

    /** Its form: the ground on top, and the slab's four upright sides. */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int[][] top = new int[LONG + 1][WIDE + 1];
        for (int column = 0; column <= LONG; column++) {
            for (int row = 0; row <= WIDE; row++) {
                final Vec3 at = at(column, row);
                top[column][row] = forming.point(at, facing(at.x(), at.z()));
            }
        }
        for (int column = 0; column < LONG; column++) {
            for (int row = 0; row < WIDE; row++) {
                forming.faced(top[column][row], top[column + 1][row], top[column + 1][row + 1]);
                forming.faced(top[column][row], top[column + 1][row + 1], top[column][row + 1]);
            }
        }
        for (int column = 0; column < LONG; column++) {
            side(forming, at(column, 0), at(column + 1, 0), new Vec3(0, 0, -1));
            side(forming, at(column, WIDE), at(column + 1, WIDE), new Vec3(0, 0, 1));
        }
        for (int row = 0; row < WIDE; row++) {
            side(forming, at(0, row), at(0, row + 1), new Vec3(-1, 0, 0));
            side(forming, at(LONG, row), at(LONG, row + 1), new Vec3(1, 0, 0));
        }
        return forming.formed();
    }

    /** The ground's point {@code column} pieces along and {@code row} across. */
    private Vec3 at(final int column, final int row) {
        final double x = across * ((double) column / LONG - 0.5);
        final double z = deep * ((double) row / WIDE - 0.5);
        return new Vec3(x, height(x, z), z);
    }

    /** The way the ground faces at a place: up, leaning away from wherever it is higher. */
    private Vec3 facing(final double x, final double z) {
        final double step = 1;
        return new Vec3(height(x - step, z) - height(x + step, z), 2 * step,
                height(x, z - step) - height(x, z + step)).unit();
    }

    /** One piece of the slab's side: from the ground's edge between two points down to the foot, facing out. */
    private void side(final Forming forming, final Vec3 one, final Vec3 other, final Vec3 out) {
        final int a = forming.point(one, out);
        final int b = forming.point(other, out);
        final int c = forming.point(new Vec3(other.x(), 0, other.z()), out);
        final int d = forming.point(new Vec3(one.x(), 0, one.z()), out);
        forming.faced(a, b, c);
        forming.faced(a, c, d);
    }
}

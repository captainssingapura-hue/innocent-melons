package com.example.fxsuite.vulkan.space;

/**
 * Four rows of four numbers that turn, move and flatten points: one matrix for each, multiplied together into one.
 * Kept in doubles, row by row; given to the card column by column, as the shader's language keeps a matrix
 * ({@link #columns}). Never changed once made.
 */
public final class Mat4 {

    private static final int SIDE = 4;

    private final double[] rows;

    /** The matrix that leaves every point where it is. */
    public Mat4() {
        this(new double[] {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});
    }

    /** Sixteen numbers, row by row. */
    public Mat4(final double[] sixteen) {
        if (sixteen.length != SIDE * SIDE) {
            throw new IllegalArgumentException("a matrix has sixteen numbers, not " + sixteen.length);
        }
        rows = sixteen.clone();
    }

    /** The matrix that moves every point {@code by}. */
    public Mat4(final Vec3 by) {
        this(new double[] {1, 0, 0, by.x(), 0, 1, 0, by.y(), 0, 0, 1, by.z(), 0, 0, 0, 1});
    }

    /**
     * The matrix that says where a point is by three ways square to each other: how far along {@code right},
     * {@code up} and {@code ahead} it lies. A camera's turning, when they are the camera's own.
     */
    public Mat4(final Vec3 right, final Vec3 up, final Vec3 ahead) {
        this(new double[] {right.x(), right.y(), right.z(), 0, up.x(), up.y(), up.z(), 0,
                ahead.x(), ahead.y(), ahead.z(), 0, 0, 0, 0, 1});
    }

    /** The number in {@code row}, {@code column}, each from 0. */
    public double at(final int row, final int column) {
        return rows[SIDE * row + column];
    }

    /** This after {@code first}: what a point comes to, {@code first} done to it and then this. */
    public Mat4 times(final Mat4 first) {
        final double[] made = new double[SIDE * SIDE];
        for (int row = 0; row < SIDE; row++) {
            for (int column = 0; column < SIDE; column++) {
                double sum = 0;
                for (int k = 0; k < SIDE; k++) {
                    sum += at(row, k) * first.at(k, column);
                }
                made[SIDE * row + column] = sum;
            }
        }
        return new Mat4(made);
    }

    /** What {@code point} comes to. */
    public Clip clip(final Vec3 point) {
        final double[] made = new double[SIDE];
        for (int row = 0; row < SIDE; row++) {
            made[row] = at(row, 0) * point.x() + at(row, 1) * point.y() + at(row, 2) * point.z() + at(row, 3);
        }
        return new Clip(made[0], made[1], made[2], made[3]);
    }

    /** Its numbers in the order the card takes them: column by column. */
    public double[] columns() {
        final double[] made = new double[SIDE * SIDE];
        for (int row = 0; row < SIDE; row++) {
            for (int column = 0; column < SIDE; column++) {
                made[SIDE * column + row] = at(row, column);
            }
        }
        return made;
    }
}

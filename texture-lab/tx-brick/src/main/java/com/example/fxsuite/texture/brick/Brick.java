package com.example.fxsuite.texture.brick;

/**
 * A brick: how long it is, how tall and how wide, in centimetres. Laid lengthwise it shows its long face, a stretcher;
 * laid across the wall, its end, a header.
 */
public record Brick(double length, double height, double width) {

    public Brick {
        if (!(length > 0 && height > 0 && width > 0)) {
            throw new IllegalArgumentException("a brick has some size every way: " + length + ", " + height + ", "
                    + width);
        }
    }

    /** The brick most walls are built of: 215 by 65 by 102.5 millimetres, two ends and a joint as long as a side. */
    public Brick() {
        this(21.5, 6.5, 10.25);
    }
}

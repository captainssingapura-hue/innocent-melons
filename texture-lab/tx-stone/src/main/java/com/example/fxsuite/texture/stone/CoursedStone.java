package com.example.fxsuite.texture.stone;

import com.example.fxsuite.texture.brick.Bond;
import com.example.fxsuite.texture.brick.Brick;
import com.example.fxsuite.texture.brick.BrickWall;
import com.example.fxsuite.texture.brick.Fleck;
import com.example.fxsuite.vulkan.term.Colour;

import java.util.List;

/**
 * Stone dressed into long blocks and laid in courses (条石): what a better house stands on, a course or two of it
 * under the brick. Blocks {@code length} by {@code height} centimetres, laid as bricks are, each half a block along
 * from the one below, with a thin joint; so it is worked out as a wall of bricks is, with a block for a brick.
 */
public record CoursedStone(double length, double height) {

    /** Blocks 90 by 30 cm. */
    public CoursedStone() {
        this(90, 30);
    }

    /** The wall of it: pale grey stone, each block a little its own shade, its grain showing dark and light. */
    public BrickWall wall() {
        return new BrickWall(new Brick(length, height, height), Bond.STRETCHER, 0.8,
                new Colour("#b5b0a5"), new Colour("#96918a"), new Colour("#d1cab8"),
                List.of(new Fleck("grain", new Colour("#5c5953"), 0.14, 0.5, 0.5, 0.35),
                        new Fleck("flecks", new Colour("#dedad0"), 0.08, 0.7, 0.7, 0.35)));
    }
}

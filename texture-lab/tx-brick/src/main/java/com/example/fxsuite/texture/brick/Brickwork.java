package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.term.Colour;

import java.util.List;

/**
 * The kinds of brickwork the lab has worked out: what the bricks are, how big, what they are laid in, and how they
 * are usually bonded. The sizes are those commonly given for each; the colours are the lab's own, to be tuned by eye.
 */
public enum Brickwork {

    /**
     * The common fired brick, red-brown, 215 by 65 by 102.5 mm, in a centimetre of pale mortar: from far off, the
     * colour the engine draws brick.
     */
    COMMON_RED,
    /**
     * The grey brick of old Chinese building (青砖): fired, then cooled starved of air, which turns it blue-grey. A
     * house's wall brick (小停泥), 288 by 64 by 144 mm, twice as long as it is wide, laid close in white lime with a
     * joint of 4 mm; usually three lengthwise and one end along every course (三顺一丁).
     */
    CHINESE_GREY,
    /**
     * Adobe (土坯): earth and chopped straw, moulded and dried in the sun, never fired. A bigger, rougher brick, 380
     * by 90 by 180 mm, bedded thick in mud the colour of the bricks, bits of straw showing pale in its face; usually
     * a course lengthwise, then a course of ends.
     */
    ADOBE;

    /** A wall of it, bonded as it usually is. */
    public BrickWall wall() {
        return wall(switch (this) {
            case COMMON_RED -> Bond.STRETCHER;
            case CHINESE_GREY -> Bond.THREE_AND_ONE;
            case ADOBE -> Bond.ENGLISH;
        });
    }

    /** A wall of it in {@code bond}. */
    public BrickWall wall(final Bond bond) {
        return switch (this) {
            case COMMON_RED -> new BrickWall(new Brick(), bond, 1,
                    new Colour("#ac5438"), new Colour("#8a3c2a"), new Colour("#b8b0a2"),
                    List.of(new Fleck("specks", new Colour("#3a2a22"), 0.12, 0.3, 0.3, 0.6)));
            case CHINESE_GREY -> new BrickWall(new Brick(28.8, 6.4, 14.4), bond, 0.4,
                    new Colour("#858c8d"), new Colour("#616a6d"), new Colour("#dcd9cf"),
                    List.of(new Fleck("pits", new Colour("#3d4446"), 0.07, 0.25, 0.25, 0.55),
                            new Fleck("bloom", new Colour("#b9bdb9"), 0.05, 0.6, 0.35, 0.35)));
            case ADOBE -> new BrickWall(new Brick(38, 9, 18), bond, 1.6,
                    new Colour("#bd9d6c"), new Colour("#a08256"), new Colour("#a68c62"),
                    List.of(new Fleck("straw", new Colour("#e2cf9c"), 0.09, 2.2, 0.22, 0.75),
                            new Fleck("grit", new Colour("#6c5a44"), 0.06, 0.4, 0.4, 0.5)));
        };
    }
}

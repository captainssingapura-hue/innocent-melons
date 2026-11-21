package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.term.Colour;

/**
 * Something scattered through a brick's face: specks of darker clay, pits, bits of straw. The face is taken as grains
 * {@code across} by {@code up} centimetres; {@code share} of them are flecks, from nothing to one; and a fleck takes
 * {@code strength} of its colour, from nothing to one.
 */
public record Fleck(String name, Colour colour, double share, double across, double up, double strength) {

    public Fleck {
        if (!(across > 0 && up > 0)) {
            throw new IllegalArgumentException(name + " has some size: " + across + " by " + up);
        }
        if (!(share >= 0 && share <= 1 && strength >= 0 && strength <= 1)) {
            throw new IllegalArgumentException(name + ": shares run from nothing to one: " + share + ", " + strength);
        }
    }

    /** The smaller way across a grain: the smallest feature a fleck adds. */
    public double smallest() {
        return Math.min(across, up);
    }
}

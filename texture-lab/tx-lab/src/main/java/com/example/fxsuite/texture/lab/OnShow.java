package com.example.fxsuite.texture.lab;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * What the bench has on show: which material; how many times as far off it is to seem as it is (1, for as it is);
 * which shapes it is looked at on, in the order they stand; and what colour the shapes are, for a material that
 * takes its colour from the thing, as paint does. The one thing the JavaFX window says to whatever serves it, as a
 * message on the bench's bus: the card's thread takes it up as its next frame begins, and the workshop makes Java's
 * own picture of it. Whole, and never changed once said: the window keeps the list of shapes that is ticked and
 * unticked, and says a new one of these each time anything changes.
 */
public record OnShow(Material material, double further, List<Shape> shapes, Vec3 tint) {

    public OnShow {
        shapes = List.copyOf(shapes);
        if (!(further >= 1)) {
            throw new IllegalArgumentException("a material seems as far off as it is, or further: " + further);
        }
    }

    /** The material on a wall, a ball and a block. */
    public OnShow(final Material material, final double further) {
        this(material, further, new Stand().shown());
    }

    /** The material on {@code shapes}, which are a deep red where the material leaves their colour to them. */
    public OnShow(final Material material, final double further, final List<Shape> shapes) {
        this(material, further, shapes, new Called("#a3151c").colour());
    }
}

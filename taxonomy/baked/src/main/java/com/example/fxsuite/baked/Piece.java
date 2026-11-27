package com.example.fxsuite.baked;

import java.util.List;

/**
 * One piece of a thing as made: what is said of it, as what made it says (a robot's piece its part and what it is of,
 * its paint); its points, three numbers each; its triangles, three points' numbers each. Its points and triangles not
 * copied, but given over: so many as a figure has are too many to copy.
 *
 * @param labels    what is said of it
 * @param points    its points, x, y and z each
 * @param triangles its triangles, three of its points each, by number
 */
public record Piece(List<String> labels, float[] points, int[] triangles) {

    /** A piece's points three numbers each, its triangles three points each, each a point it has. */
    public Piece {
        labels = List.copyOf(labels);
        if (points.length % 3 != 0 || triangles.length % 3 != 0) {
            throw new IllegalArgumentException("a piece's points and triangles go by threes: " + points.length
                    + " numbers, " + triangles.length + " corners");
        }
        final int count = points.length / 3;
        for (final int corner : triangles) {
            if (corner < 0 || corner >= count) {
                throw new IllegalArgumentException("a triangle's corner " + corner + ", of " + count + " points");
            }
        }
    }
}

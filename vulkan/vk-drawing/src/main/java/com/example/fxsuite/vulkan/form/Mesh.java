package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A form as triangles, in the frame of the thing it is the form of: its points, at each point the way its surface
 * faces there (one long), and its triangles, three corners each, a corner the number of a point. Every triangle is
 * wound so that its second side crossed with its third faces out of the form ({@link Vec3#cross}); the camera's own
 * test says which way round that shows on the card. Never changed once made.
 *
 * <p>Each point also says where on the thing's surface it is ({@code ons}), which is what the surface's terms are
 * worked out from. For most things that is simply where the point is. For a thing whose surface is laid round it,
 * as a wall's bricks are laid round a house, it is how far along the surface and how far up, so the one pattern
 * runs on round a corner and over an edge.
 */
public record Mesh(List<Vec3> points, List<Vec3> facings, List<Integer> corners, List<Vec3> ons) {

    private static final int CORNERS = 3;

    public Mesh {
        points = List.copyOf(points);
        facings = List.copyOf(facings);
        corners = List.copyOf(corners);
        ons = List.copyOf(ons);
        if (points.size() != facings.size() || points.size() != ons.size()) {
            throw new IllegalArgumentException("each point faces one way and is at one place on the surface: "
                    + points.size() + " points, " + facings.size() + " ways, " + ons.size() + " places");
        }
        if (corners.size() % CORNERS != 0) {
            throw new IllegalArgumentException("a triangle has three corners: " + corners.size() + " corners in all");
        }
        for (final int corner : corners) {
            if (corner < 0 || corner >= points.size()) {
                throw new IllegalArgumentException("a corner at point " + corner + ", of " + points.size());
            }
        }
    }

    /** A form each of whose points is, on its surface, where it is. */
    public Mesh(final List<Vec3> points, final List<Vec3> facings, final List<Integer> corners) {
        this(points, facings, corners, points);
    }

    public int triangles() {
        return corners.size() / CORNERS;
    }

    /** Where corner {@code which} (0, 1 or 2) of {@code triangle} is. */
    public Vec3 corner(final int triangle, final int which) {
        return points.get(corners.get(CORNERS * triangle + which));
    }

    /** The way the surface faces at corner {@code which} of {@code triangle}. */
    public Vec3 facing(final int triangle, final int which) {
        return facings.get(corners.get(CORNERS * triangle + which));
    }

    /** Where on the thing's surface corner {@code which} of {@code triangle} is. */
    public Vec3 on(final int triangle, final int which) {
        return ons.get(corners.get(CORNERS * triangle + which));
    }

    /** {@code triangle}'s second side crossed with its third: the way it faces, twice as long as it is big. */
    public Vec3 crossed(final int triangle) {
        final Vec3 first = corner(triangle, 0);
        return corner(triangle, 1).minus(first).cross(corner(triangle, 2).minus(first));
    }

    /** How big {@code triangle} is, in square centimetres. */
    public double area(final int triangle) {
        return crossed(triangle).length() / 2;
    }

    /** How big its whole surface is, in square centimetres. */
    public double area() {
        double sum = 0;
        for (int triangle = 0; triangle < triangles(); triangle++) {
            sum += area(triangle);
        }
        return sum;
    }
}

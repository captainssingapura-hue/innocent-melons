package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Several meshes made one, each first moved where its matrix takes it: its points moved, the way its surface faces
 * turned with it, and where on its surface each point is ({@code on}) left as it was, so whatever the surface's
 * terms draw lies on the merged mesh as it lay on the part. What a copy drawn by the card does for one draw, done
 * once, ahead, for all: so the one mesh is drawn as the copies were, in one draw, with nothing told for each part.
 * A matrix is to turn, move and size alike every way, never stretch, or the way the surface faces comes out wrong.
 *
 * @param parts the meshes, each with where it is taken
 */
public record Merged(List<Placed> parts) {

    /** A mesh, and what takes it from its own frame to where it stands in the whole. */
    public record Placed(Mesh mesh, Mat4 placed) {
    }

    public Merged {
        parts = List.copyOf(parts);
    }

    /** The one mesh: every part's points after the last, its corners counted on from where its points begin. */
    public Mesh mesh() {
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>();
        final List<Vec3> ons = new ArrayList<>();
        for (final Placed part : parts) {
            final int first = points.size();
            final Mat4 m = part.placed();
            for (int point = 0; point < part.mesh().points().size(); point++) {
                points.add(moved(m, part.mesh().points().get(point), 1));
                facings.add(moved(m, part.mesh().facings().get(point), 0).unit());
                ons.add(part.mesh().ons().get(point));
            }
            part.mesh().corners().forEach(corner -> corners.add(first + corner));
        }
        return new Mesh(points, facings, corners, ons);
    }

    /** {@code v} taken by {@code m}: as a point, moved as well as turned, where {@code w} is one; turned only, at 0. */
    private Vec3 moved(final Mat4 m, final Vec3 v, final double w) {
        return new Vec3(m.at(0, 0) * v.x() + m.at(0, 1) * v.y() + m.at(0, 2) * v.z() + m.at(0, 3) * w,
                m.at(1, 0) * v.x() + m.at(1, 1) * v.y() + m.at(1, 2) * v.z() + m.at(1, 3) * w,
                m.at(2, 0) * v.x() + m.at(2, 1) * v.y() + m.at(2, 2) * v.z() + m.at(2, 3) * w);
    }
}

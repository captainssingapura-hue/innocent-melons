package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * What carries something made in a frame of its own into another, turned and moved but not stretched, as an engine
 * made front to back along its crank is carried into a car, turned end for end and set where it goes: each part's
 * form and points carried, the parts joined as they were. Put together, the carried platform comes out where it was
 * carried to, as it would have come out in its own frame.
 *
 * @param by what carries a point from the one frame to the other: a turn and a move
 */
public record Carrying(Mat4 by) {

    /** {@code platform}, every part of it carried, joined as it was. */
    public Platform carried(final Platform platform) {
        final Map<Part, Part> carried = new IdentityHashMap<>();
        platform.parts().forEach(p -> carried.put(p, carried(p)));
        return new Platform(platform.name(), carried.get(platform.root()), platform.connections().stream()
                .map(c -> new Connection(c.name(), carried.get(c.carrier()), c.at(), carried.get(c.carried()), c.to(),
                        c.joining())).toList());
    }

    /** {@code part}, its form and its points carried. */
    public Part carried(final Part part) {
        return new Part(part.name(), part.group(), part.paired(), part.points().stream()
                .map(p -> new Point(p.name(), at(p.at()))).toList(), carried(part.form()), part.made());
    }

    /** Where {@code p} is carried to. */
    public Vec3 at(final Vec3 p) {
        return way(p).plus(new Vec3(by.at(0, 3), by.at(1, 3), by.at(2, 3)));
    }

    /** Which way {@code v} points, carried: turned, not moved. */
    public Vec3 way(final Vec3 v) {
        return new Vec3(by.at(0, 0) * v.x() + by.at(0, 1) * v.y() + by.at(0, 2) * v.z(),
                by.at(1, 0) * v.x() + by.at(1, 1) * v.y() + by.at(1, 2) * v.z(),
                by.at(2, 0) * v.x() + by.at(2, 1) * v.y() + by.at(2, 2) * v.z());
    }

    private Mesh carried(final Mesh mesh) {
        final List<Vec3> points = mesh.points().stream().map(this::at).toList();
        final List<Vec3> facings = mesh.facings().stream().map(this::way).toList();
        final List<Vec3> ons = mesh.ons().stream().map(this::at).toList();
        return new Mesh(points, facings, mesh.corners(), ons);
    }
}

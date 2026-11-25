package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One part of the platform: what it is called, which group it belongs to, whether there is one on each side, the
 * points it is joined at, by name, its form, and what it is made of; its points and its form in its own frame. A part
 * on each side is said as the right one; the left is its mirror. A part down the middle that is joined to both sides
 * names its points on each side {@code ", right"} and {@code ", left"}.
 *
 * @param name   what it is called
 * @param group  which group it belongs to
 * @param paired whether there is one on each side
 * @param points where it is joined
 * @param form   its form
 * @param made   what it is made of, and whether its skin is bare
 */
public record Part(String name, Group group, boolean paired, List<Point> points, Mesh form, Made made) {

    public Part {
        points = List.copyOf(points);
        if (name == null || group == null || form == null || made == null) {
            throw new IllegalArgumentException("a part is named, of some group, has a form and is made of something: "
                    + name);
        }
        final Set<String> named = new HashSet<>();
        for (final Point p : points) {
            if (!named.add(p.name())) {
                throw new IllegalArgumentException(name + ": two points called " + p.name());
            }
        }
    }

    /** Whether {@code other} is this part: a part is itself, not any other of the same shape. */
    @Override
    public boolean equals(final Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /** Its point called {@code named}; refused if it has none. */
    public Point point(final String named) {
        return points.stream().filter(p -> p.name().equals(named)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(name + " has no point called " + named));
    }

    /** Whether it has a point called {@code named}. */
    public boolean has(final String named) {
        return points.stream().anyMatch(p -> p.name().equals(named));
    }

    /** The same, on the other side of the middle: its points and its form mirrored. */
    public Part mirrored() {
        return new Part(name, group, paired, points.stream().map(Point::mirrored).toList(), mirrored(form), made);
    }

    /** {@code mesh} mirrored across the middle, its triangles turned so they still face out. */
    private Mesh mirrored(final Mesh mesh) {
        final List<Integer> corners = new ArrayList<>(mesh.corners().size());
        for (int k = 0; k + 2 < mesh.corners().size(); k += 3) {
            corners.add(mesh.corners().get(k));
            corners.add(mesh.corners().get(k + 2));
            corners.add(mesh.corners().get(k + 1));
        }
        return new Mesh(flipped(mesh.points()), flipped(mesh.facings()), corners, flipped(mesh.ons()));
    }

    private List<Vec3> flipped(final List<Vec3> all) {
        return all.stream().map(v -> new Vec3(-v.x(), v.y(), v.z())).toList();
    }
}

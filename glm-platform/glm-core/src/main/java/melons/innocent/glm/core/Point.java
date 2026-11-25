package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A point of a part, where it is joined to another: its name, and where it is in the part's own frame, in centimetres,
 * across as x (to the right), up as y, along as z (ahead).
 *
 * @param name what it is called
 * @param at   where it is
 */
public record Point(String name, Vec3 at) {

    public Point {
        if (name == null || name.isBlank() || at == null) {
            throw new IllegalArgumentException("a point is named and is somewhere: " + name);
        }
    }

    /** The same on the other side of the middle. */
    public Point mirrored() {
        return new Point(name, new Vec3(-at.x(), at.y(), at.z()));
    }
}

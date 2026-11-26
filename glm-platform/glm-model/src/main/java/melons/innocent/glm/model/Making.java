package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;

import java.util.List;
import java.util.Map;

/**
 * A part made of {@code made} where it stands in the platform's frame and given its own: its points and its form,
 * worked out where they are on the platform, taken back to its first point, so that put together it stands where it
 * was made.
 */
record Making(Made made) {

    /** The platform's root, its own frame the platform's: its points and its form where they are on the platform. */
    Part root(final String name, final Group group, final Map<String, Vec3> points, final Mesh form) {
        return new Part(name, group, false, points.entrySet().stream().map(e -> new Point(e.getKey(), e.getValue()))
                .toList(), form, made);
    }

    /**
     * A part called {@code name}, its points where {@code points} has them on the platform, in order, the first the
     * one its own frame is taken back to; its form {@code form} where it is on the platform.
     */
    Part part(final String name, final Group group, final boolean paired, final Map<String, Vec3> points,
              final Mesh form) {
        final Vec3 anchor = points.values().iterator().next();
        final List<Point> own = points.entrySet().stream().map(e -> new Point(e.getKey(), e.getValue().minus(anchor)))
                .toList();
        return new Part(name, group, paired, own, new Merged(List.of(new Merged.Placed(form,
                new Mat4(anchor.times(-1))))).mesh(), made);
    }
}

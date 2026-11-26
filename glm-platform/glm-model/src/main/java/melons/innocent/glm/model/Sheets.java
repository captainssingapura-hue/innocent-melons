package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.Collections;
import java.util.List;

/**
 * Sheet steel as a body shop presses and welds it, {@code skin} thick: flat panels square to the body's own ways,
 * across it, level, or upright along it; and box members pressed to a section and bent along a line, as its rails and
 * cross members are.
 *
 * @param skin how thick its panels are
 */
record Sheets(double skin) {

    private static final int ROUND = 16;
    private static final Vec3 ACROSS = new Vec3(1, 0, 0);
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 ALONG = new Vec3(0, 0, 1);

    /** A panel across the body at {@code z}, its outline across and up. */
    Mesh across(final double z, final List<Spot> outline, final List<List<Spot>> holes) {
        return new Plate(outline, holes, new Vec3(0, 0, z), ACROSS, UP, skin).mesh();
    }

    /** A panel across the body at {@code z}, from {@code x0} to {@code x1} across and {@code y0} to {@code y1} up. */
    Mesh across(final double z, final double x0, final double y0, final double x1, final double y1) {
        return across(z, rectangle(x0, y0, x1, y1), List.of());
    }

    /** A level panel at {@code y}, from {@code x0} to {@code x1} across and {@code z0} to {@code z1} along. */
    Mesh level(final double y, final double x0, final double z0, final double x1, final double z1) {
        return level(y, rectangle(x0, z0, x1, z1));
    }

    /** A level panel at {@code y}, its outline across and along. */
    Mesh level(final double y, final List<Spot> outline) {
        return new Plate(outline, List.of(), new Vec3(0, y, 0), ACROSS, ALONG, skin).mesh();
    }

    /** An upright panel along the body at {@code x}, its outline along and up. */
    Mesh upright(final double x, final List<Spot> outline) {
        return new Plate(outline, List.of(), new Vec3(x, 0, 0), ALONG, UP, skin).mesh();
    }

    /** An upright panel at {@code x}, from {@code z0} to {@code z1} along and {@code y0} to {@code y1} up. */
    Mesh upright(final double x, final double z0, final double y0, final double z1, final double y1) {
        return upright(x, rectangle(z0, y0, z1, y1));
    }

    /** A box member pressed to {@code section} along {@code through}. */
    Mesh member(final List<Vec3> through, final Section section) {
        return member(through, Collections.nCopies(through.size(), section));
    }

    /** A box member along {@code through}, pressed to each of {@code sections} in turn. */
    Mesh member(final List<Vec3> through, final List<Section> sections) {
        return new Swept(new Course(through, 8), sections, ROUND).mesh();
    }

    /** {@code points} on the right, mirrored to the left if {@code side} is -1. */
    List<Vec3> sided(final List<Vec3> points, final int side) {
        return points.stream().map(p -> new Vec3(side * p.x(), p.y(), p.z())).toList();
    }

    /** A rectangle from {@code (u0, v0)} to {@code (u1, v1)}. */
    List<Spot> rectangle(final double u0, final double v0, final double u1, final double v1) {
        return List.of(new Spot(Math.min(u0, u1), Math.min(v0, v1)), new Spot(Math.max(u0, u1), Math.min(v0, v1)),
                new Spot(Math.max(u0, u1), Math.max(v0, v1)), new Spot(Math.min(u0, u1), Math.max(v0, v1)));
    }
}

package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.road.surface.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * The concrete what carries a road is built of, each piece a solid placed by the road's {@code course}: laid along it,
 * following it, so far across from its line and so high over it; or standing upright where it is, from so high to so
 * high. In centimetres: across, along, up.
 *
 * @param course what the road is laid along
 */
record Blocks(Course course) {

    /**
     * A piece laid along its course, from {@code s0} along to {@code s1}, from {@code x0} across to {@code x1}, and
     * from {@code y0} over its course to {@code y1}.
     */
    Part laid(final Leaf<?> element, final String name, final double x0, final double x1, final double s0,
              final double s1, final double y0, final double y1) {
        return part(element, name, List.of(course.beside(s0, x0, y0), course.beside(s0, x1, y0),
                course.beside(s1, x0, y0), course.beside(s1, x1, y0), course.beside(s0, x0, y1),
                course.beside(s0, x1, y1), course.beside(s1, x0, y1), course.beside(s1, x1, y1)));
    }

    /**
     * A piece laid along its course as {@link #laid} has it, its top {@code y1} over its course, but its foot standing
     * {@code foot} high wherever it is, as the ground has it.
     */
    Part footed(final Leaf<?> element, final String name, final double x0, final double x1, final double s0,
                final double s1, final double foot, final double y1) {
        final List<Point> top = List.of(course.beside(s0, x0, y1), course.beside(s0, x1, y1),
                course.beside(s1, x0, y1), course.beside(s1, x1, y1));
        final List<Point> corners = new ArrayList<>();
        top.forEach(p -> corners.add(new Point(p.across(), p.along(), foot)));
        corners.addAll(top);
        return part(element, name, corners);
    }

    /**
     * A piece standing upright {@code s} along its course and {@code x} across from its line, {@code across} by
     * {@code along} round it, squared with the course there, from {@code foot} high to {@code top} high.
     */
    Part upright(final Leaf<?> element, final String name, final double s, final double x, final double across,
                 final double along, final double foot, final double top) {
        final double h = course.heading(s);
        final Point middle = course.beside(s, x, 0);
        final List<Point> plan = new ArrayList<>(4);
        for (int l = -1; l <= 1; l += 2) {
            for (int a = -1; a <= 1; a += 2) {
                final double dx = a * across / 2;
                final double dz = l * along / 2;
                plan.add(new Point(middle.across() + dx * Math.cos(h) + dz * Math.sin(h),
                        middle.along() - dx * Math.sin(h) + dz * Math.cos(h), 0));
            }
        }
        return upright(element, name, plan, foot, top);
    }

    /** A piece standing upright over {@code plan}, its four corners from above, from {@code foot} to {@code top}. */
    Part upright(final Leaf<?> element, final String name, final List<Point> plan, final double foot,
                 final double top) {
        final List<Point> corners = new ArrayList<>(8);
        plan.forEach(p -> corners.add(new Point(p.across(), p.along(), foot)));
        plan.forEach(p -> corners.add(new Point(p.across(), p.along(), top)));
        return part(element, name, corners);
    }

    private Part part(final Leaf<?> element, final String name, final List<Point> corners) {
        return new Part(element, new Solid(name, Role.STRUCTURE, Material.CONCRETE, corners));
    }
}

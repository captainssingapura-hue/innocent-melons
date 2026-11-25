package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A bar forged or rolled to a section of any outline, an I-beam say, carried along its course: the section drawn
 * across and up, round to the left, kept upright as the course bends, changing from one point of the course to the
 * next; its faces flat between the section's corners, as a forging's are; its two ends closed flat, square to its
 * line.
 *
 * @param course   the line it follows
 * @param sections its section at each of its course's points, as many as there are, each as many corners as the next
 * @param across   the way its sections' across always runs, square to its course, for a bar that bends in one plane
 *                 only, as a connecting rod does; or none, to keep them upright
 */
public record Profiled(Course course, List<List<Spot>> sections, Vec3 across) {

    public Profiled {
        final List<List<Spot>> copied = sections.stream().map(List::copyOf).toList();
        sections = copied;
        final int corners = copied.getFirst().size();
        across = across == null ? null : across.unit();
        if (copied.size() != course.through().size()
                || copied.stream().anyMatch(s -> s.size() != corners || s.size() < 3)) {
            throw new IllegalArgumentException("a section at each point of the course, each as many corners");
        }
    }

    /** One kept upright as it bends. */
    public Profiled(final Course course, final List<List<Spot>> sections) {
        this(course, sections, null);
    }

    /** It. */
    public Mesh mesh() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final List<Vec3> points = course.points();
        final List<Vec3> ways = course.ways();
        final List<Double> shares = course.shares();
        final List<List<Vec3>> rings = new ArrayList<>();
        final List<Vec3[]> uprights = new ArrayList<>();
        for (int k = 0; k < points.size(); k++) {
            final Vec3[] upright = upright(ways.get(k));
            uprights.add(upright);
            rings.add(placed(section(shares.get(k)), points.get(k), upright));
        }
        for (int r = 0; r + 1 < rings.size(); r++) {
            final List<Spot> drawn = section(shares.get(r));
            for (int k = 0; k < drawn.size(); k++) {
                final int next = (k + 1) % drawn.size();
                final Spot way = drawn.get(k).to(drawn.get(next));
                final Vec3[] u = uprights.get(r);
                final Vec3 out = u[0].times(way.v()).minus(u[1].times(way.u()));
                final int a = forming.point(rings.get(r).get(k), out);
                final int b = forming.point(rings.get(r).get(next), out);
                final int c = forming.point(rings.get(r + 1).get(next), out);
                final int d = forming.point(rings.get(r + 1).get(k), out);
                forming.faced(a, b, c);
                forming.faced(a, c, d);
            }
        }
        closed(forming, rings.getFirst(), section(0), ways.getFirst().times(-1));
        closed(forming, rings.getLast(), section(shares.getLast()), ways.getLast());
        return forming.formed();
    }

    /** Its section {@code share} along: past point {@code (int) share}, so far toward the next, drawn to the left. */
    private List<Spot> section(final double share) {
        final int k = Math.min((int) Math.floor(share), sections.size() - 2);
        final double t = share - k;
        final List<Spot> from = sections.get(k);
        final List<Spot> to = sections.get(k + 1);
        final List<Spot> mixed = new ArrayList<>();
        for (int i = 0; i < from.size(); i++) {
            mixed.add(new Spot(from.get(i).u() + (to.get(i).u() - from.get(i).u()) * t,
                    from.get(i).v() + (to.get(i).v() - from.get(i).v()) * t));
        }
        return new EarClipping(mixed, List.of()).spots();
    }

    private List<Vec3> placed(final List<Spot> section, final Vec3 middle, final Vec3[] upright) {
        return section.stream().map(s -> middle.plus(upright[0].times(s.u())).plus(upright[1].times(s.v())))
                .toList();
    }

    /** An end closed flat, facing {@code out}. */
    private void closed(final Forming forming, final List<Vec3> ring, final List<Spot> drawn, final Vec3 out) {
        final EarClipping cut = new EarClipping(drawn, List.of());
        final int[] at = new int[ring.size()];
        for (int k = 0; k < ring.size(); k++) {
            at[k] = forming.point(ring.get(k), out);
        }
        final List<Integer> triangles = cut.triangles();
        for (int k = 0; k + 2 < triangles.size(); k += 3) {
            forming.faced(at[triangles.get(k)], at[triangles.get(k + 1)], at[triangles.get(k + 2)]);
        }
    }

    /** Across and up, square to {@code way}, up kept as near the world's up as it can be. */
    private Vec3[] upright(final Vec3 way) {
        if (across != null) {
            return new Vec3[]{across, across.cross(way).unit()};
        }
        final Vec3 up = Math.abs(way.y()) > 0.95 ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0);
        final Vec3 across = way.cross(up).unit();
        return new Vec3[]{across, across.cross(way).unit()};
    }
}

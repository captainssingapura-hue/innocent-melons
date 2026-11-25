package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A tube pressed to its shape, as a hydroformed tube is: its section carried along its course, kept upright as it
 * bends, and changing from one point of the course to the next, as the die widens it, narrows it or flattens it; its
 * two ends closed flat, square to its line, where it is welded to what it meets. Its skin faces out all round.
 *
 * @param course   the line it follows
 * @param sections its section at each of its course's points, as many as there are
 * @param round    how many points round each section is drawn in
 */
public record Swept(Course course, List<Section> sections, int round) {

    public Swept {
        sections = List.copyOf(sections);
        if (sections.size() != course.through().size() || round < 3) {
            throw new IllegalArgumentException("a swept tube has a section at each point of its course: "
                    + sections.size() + " for " + course.through().size());
        }
    }

    /** It. */
    public Mesh mesh() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final List<Vec3> points = course.points();
        final List<Vec3> ways = course.ways();
        final List<Double> shares = course.shares();
        final List<int[]> rings = new ArrayList<>();
        for (int k = 0; k < points.size(); k++) {
            rings.add(ring(forming, points.get(k), ways.get(k), section(shares.get(k))));
        }
        for (int r = 0; r + 1 < rings.size(); r++) {
            final int[] a = rings.get(r);
            final int[] b = rings.get(r + 1);
            for (int k = 0; k < round; k++) {
                final int next = (k + 1) % round;
                forming.faced(a[k], b[k], b[next]);
                forming.faced(a[k], b[next], a[next]);
            }
        }
        closed(forming, points.getFirst(), ways.getFirst().times(-1), section(0));
        closed(forming, points.getLast(), ways.getLast(), section(shares.getLast()));
        return forming.formed();
    }

    /** Its section {@code share} along: past point {@code (int) share}, so far toward the next. */
    private Section section(final double share) {
        final int k = Math.min((int) Math.floor(share), sections.size() - 2);
        return sections.get(k).toward(sections.get(k + 1), share - k);
    }

    /** A ring of its skin at {@code middle}, its line running {@code way} there: its points' numbers. */
    private int[] ring(final Forming forming, final Vec3 middle, final Vec3 way, final Section section) {
        final Vec3[] upright = upright(way);
        final int[] made = new int[round];
        for (int k = 0; k < round; k++) {
            final double turn = 2 * Math.PI * k / round;
            final double[] at = section.at(turn);
            final double[] facing = section.facing(turn);
            made[k] = forming.point(middle.plus(upright[0].times(at[0])).plus(upright[1].times(at[1])),
                    upright[0].times(facing[0]).plus(upright[1].times(facing[1])));
        }
        return made;
    }

    /** An end closed flat at {@code middle}, facing {@code out}. */
    private void closed(final Forming forming, final Vec3 middle, final Vec3 out, final Section section) {
        final Vec3[] upright = upright(out);
        final int centre = forming.point(middle, out);
        final int[] at = new int[round];
        for (int k = 0; k < round; k++) {
            final double[] p = section.at(2 * Math.PI * k / round);
            at[k] = forming.point(middle.plus(upright[0].times(p[0])).plus(upright[1].times(p[1])), out);
        }
        for (int k = 0; k < round; k++) {
            forming.faced(centre, at[k], at[(k + 1) % round]);
        }
    }

    /** Across and up, square to {@code way}, up kept as near the world's up as it can be. */
    private Vec3[] upright(final Vec3 way) {
        final Vec3 up = Math.abs(way.y()) > 0.95 ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0);
        final Vec3 across = way.cross(up).unit();
        return new Vec3[]{across, across.cross(way).unit()};
    }
}

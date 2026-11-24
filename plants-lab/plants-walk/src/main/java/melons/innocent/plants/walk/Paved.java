package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.road.surface.Section;
import melons.innocent.road.surface.Strip;

import java.util.ArrayList;
import java.util.List;

/**
 * A road's surface laid along a road that winds: the road works' section ({@link Section}: its strips across, lanes
 * and shoulders, its pavement falling away from its line, whether lines are painted on it) swept along the road's
 * curve, where the road works themselves lay it along a straight stretch only. From the one section and the one
 * road come its pavement as a mesh, standing on the ground with its edges closed down to it; the lines painted on
 * it as another; and what of it is stood on, a low flat block to each strip of each short run.
 *
 * <p>The lines are the road works' own rule, said again here because theirs is laid straight: between two lanes
 * going opposite ways a broken centre line, between two going the same way a lane line broken more widely, and
 * where a lane meets a shoulder an unbroken edge line along the lane's own edge. Their widths and strokes are the
 * road works' figures, copied: when the road works lay a section along a curve, this goes and theirs is used.
 *
 * @param road    the road it is laid along
 * @param section its surface across
 */
public record Paved(Road road, Section section) {

    /** How far apart the pavement's points are along the road, the lines', and a run stood on, in centimetres. */
    private static final double PIECE = 250;
    private static final double STROKE = 50;
    private static final double RUN = 500;
    /** How thick paint lies; a line between lanes, and along a lane's edge, how wide; a stroke, and the gaps. */
    private static final double PAINT = 1;
    private static final double LINE = 10;
    private static final double EDGE = 15;
    private static final double DASH = 300;
    private static final double CENTRE_GAP = 300;
    private static final double LANE_GAP = 600;

    /** A line painted along the road: from how far across to how far, and how long its gaps are; none, unbroken. */
    private record Line(double from, double to, double gap) {
    }

    /** Where the spot so far {@code across} from the road's line and so {@code high} is, so far {@code along}. */
    private Vec3 at(final double along, final double across, final double high) {
        final Vec3 beside = new Vec3(1, 0, -road.slope(along)).unit();
        return new Vec3(road.across(along), high, along).plus(beside.times(across));
    }

    /** Its pavement: every strip's top, and a skirt down to the ground along each edge. */
    public Mesh pavement() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Vec3 up = new Vec3(0, 1, 0);
        final int edges = section.strips().size() + 1;
        final List<int[]> rows = new ArrayList<>();
        for (double along = road.from(); along <= road.to(); along += PIECE) {
            final Vec3 beside = new Vec3(1, 0, -road.slope(along)).unit();
            final int[] row = new int[edges + 4];
            for (int edge = 0; edge < edges; edge++) {
                final double across = section.edge(edge);
                row[edge] = forming.point(at(along, across, section.top(across)), up);
            }
            final double left = section.edge(0);
            final double right = section.edge(edges - 1);
            row[edges] = forming.point(at(along, left, section.top(left)), beside.times(-1));
            row[edges + 1] = forming.point(at(along, left, 0), beside.times(-1));
            row[edges + 2] = forming.point(at(along, right, section.top(right)), beside);
            row[edges + 3] = forming.point(at(along, right, 0), beside);
            rows.add(row);
        }
        for (int piece = 0; piece + 1 < rows.size(); piece++) {
            final int[] here = rows.get(piece);
            final int[] next = rows.get(piece + 1);
            for (int edge = 0; edge + 1 < edges; edge++) {
                quad(forming, here[edge], here[edge + 1], next[edge + 1], next[edge]);
            }
            quad(forming, here[edges], here[edges + 1], next[edges + 1], next[edges]);
            quad(forming, here[edges + 2], here[edges + 3], next[edges + 3], next[edges + 2]);
        }
        return forming.formed();
    }

    /** The lines painted on it, a hair over its pavement; nothing, on a section that is not marked. */
    public Mesh lines() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Vec3 up = new Vec3(0, 1, 0);
        final double first = Math.ceil(road.from() / STROKE) * STROKE;
        for (final Line line : painted()) {
            for (double along = first; along + STROKE <= road.to(); along += STROKE) {
                final double through = along - Math.floor(along / (DASH + line.gap())) * (DASH + line.gap());
                if (line.gap() > 0 && through >= DASH) {
                    continue;
                }
                quad(forming, forming.point(at(along, line.from(), section.top(line.from()) + PAINT), up),
                        forming.point(at(along, line.to(), section.top(line.to()) + PAINT), up),
                        forming.point(at(along + STROKE, line.to(), section.top(line.to()) + PAINT), up),
                        forming.point(at(along + STROKE, line.from(), section.top(line.from()) + PAINT), up));
            }
        }
        return forming.formed();
    }

    /** Which lines are painted on it, by what lies either side of where two strips meet. */
    private List<Line> painted() {
        final List<Line> lines = new ArrayList<>();
        final List<Strip> strips = section.strips();
        for (int k = 1; k < strips.size() && section.marked(); k++) {
            final Strip before = strips.get(k - 1);
            final Strip after = strips.get(k);
            final double at = section.edge(k);
            if (before.kind() == Strip.Kind.LANE && after.kind() == Strip.Kind.LANE) {
                lines.add(new Line(at - LINE / 2, at + LINE / 2,
                        before.against() == after.against() ? LANE_GAP : CENTRE_GAP));
            } else if (before.kind() == Strip.Kind.LANE) {
                lines.add(new Line(at - EDGE, at, 0));
            } else if (after.kind() == Strip.Kind.LANE) {
                lines.add(new Line(at, at + EDGE, 0));
            }
        }
        return lines;
    }

    /** Two triangles for the four points, facing as the first of them faces. */
    private void quad(final Forming forming, final int a, final int b, final int c, final int d) {
        forming.faced(a, b, c);
        forming.faced(a, c, d);
    }

    /**
     * What of it is stood on from {@code from} to {@code to} along: a flat block to each paved strip of each short
     * run, as high as the strip's higher edge, so the road is stepped up onto and its crown is a little higher.
     */
    public List<Block> stoodOn(final double from, final double to) {
        final List<Block> blocks = new ArrayList<>();
        for (double along = Math.max(from, road.from()); along + RUN <= Math.min(to, road.to()); along += RUN) {
            for (int k = 0; k < section.strips().size(); k++) {
                final double left = section.edge(k);
                final double right = section.edge(k + 1);
                if (!section.strips().get(k).paved()) {
                    continue;
                }
                final Vec3 begins = at(along, (left + right) / 2, 0);
                final Vec3 ends = at(along + RUN, (left + right) / 2, 0);
                final Vec3 way = ends.minus(begins).unit();
                final Vec3 middle = begins.plus(ends).times(0.5);
                // the engine turns a block as it turns anything about the upright: its own length then runs this way
                blocks.add(new Block(middle.x(), middle.z(), ends.minus(begins).length() / 2, (right - left) / 2,
                        Math.toDegrees(Math.atan2(-way.z(), way.x())), 0,
                        Math.max(section.top(left), section.top(right))));
            }
        }
        return blocks;
    }
}

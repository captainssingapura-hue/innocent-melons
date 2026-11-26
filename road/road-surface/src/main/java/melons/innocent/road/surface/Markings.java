package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import melons.innocent.forms.Tint;

import java.util.ArrayList;
import java.util.List;

/**
 * The lines painted on a road, in white, {@link #THICK} thick on its pavement, where its strips meet: between two lanes
 * going the same way, a lane line, broken; between two going opposite ways, the centre line, broken more closely;
 * where a lane meets anything else, a shoulder, a kerb or a barrier, an edge line, unbroken, along the lane's own
 * edge. None on a section that is not marked. Each stroke follows its course, parted wherever its pavement is, at
 * {@code cuts}.
 *
 * @param section the road's section
 * @param course  what the road is laid along
 * @param cuts    where its pavement is parted, so far along it
 */
record Markings(Section section, Course course, List<Double> cuts) {

    /** How thick paint lies; its colour. */
    static final double THICK = 1;
    static final Tint WHITE = new Tint("#f2f2ec");
    /** A line: how wide it is, how long each stroke of it and each gap. */
    record Line(String name, double wide, double stroke, double gap) {
    }

    static final Line LANE = new Line("lane line", 10, 300, 600);
    static final Line CENTRE = new Line("centre line", 10, 300, 300);
    static final Line EDGE = new Line("edge line", 15, 0, 0);

    /** Its lines along its course, from end to end. */
    List<Part> along() {
        final List<Part> parts = new ArrayList<>();
        if (!section.marked()) {
            return parts;
        }
        final List<Strip> strips = section.strips();
        for (int k = 1; k < strips.size(); k++) {
            final Strip before = strips.get(k - 1);
            final Strip after = strips.get(k);
            final double at = section.edge(k);
            final boolean lanes = before.kind() == Strip.Kind.LANE && after.kind() == Strip.Kind.LANE;
            if (lanes) {
                final Line line = before.against() == after.against() ? LANE : CENTRE;
                parts.addAll(painted(line, at - line.wide() / 2));
            } else if (before.kind() == Strip.Kind.LANE) {
                parts.addAll(painted(EDGE, at - EDGE.wide()));
            } else if (after.kind() == Strip.Kind.LANE) {
                parts.addAll(painted(EDGE, at));
            }
        }
        return parts;
    }

    /** {@code line}, from {@code x} across, along its course: unbroken, or stroke by stroke. */
    private List<Part> painted(final Line line, final double x) {
        final List<Part> parts = new ArrayList<>();
        final double end = course.length();
        if (line.stroke() <= 0) {
            parts.addAll(stroke(line, x, 0, end));
            return parts;
        }
        for (double s = 0; s < end; s += line.stroke() + line.gap()) {
            parts.addAll(stroke(line, x, s, Math.min(s + line.stroke(), end)));
        }
        return parts;
    }

    /** A stroke of {@code line} from {@code s0} along to {@code s1}, parted wherever its pavement is. */
    private List<Part> stroke(final Line line, final double x, final double s0, final double s1) {
        final List<Part> parts = new ArrayList<>();
        double from = s0;
        for (final double cut : cuts) {
            if (cut > from + 1e-6 && cut < s1 - 1e-6) {
                parts.add(stroke(line, x, new Shapes.Run(from, cut)));
                from = cut;
            }
        }
        parts.add(stroke(line, x, new Shapes.Run(from, s1)));
        return parts;
    }

    private Part stroke(final Line line, final double x, final Shapes.Run run) {
        final double x1 = x + line.wide();
        final double y0 = section.top(x);
        final double y1 = section.top(x1);
        return new Part(new Elements.Marking(), new Shapes(course).solid(line.name(), Role.DRESSING, Material.PAINTED,
                run, x, y0, x1, y1, y0 + THICK, y1 + THICK).tinted(WHITE));
    }
}

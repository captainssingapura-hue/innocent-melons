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
 * edge. None on a section that is not marked.
 *
 * @param section the road's section
 * @param level   how high what it stands on is
 */
record Markings(Section section, double level) {

    /** How thick paint lies; its colour. */
    static final double THICK = 1;
    static final Tint WHITE = new Tint("#f2f2ec");
    /** A line: how wide it is, how long each stroke of it and each gap. */
    record Line(String name, double wide, double stroke, double gap) {
    }

    static final Line LANE = new Line("lane line", 10, 300, 600);
    static final Line CENTRE = new Line("centre line", 10, 300, 300);
    static final Line EDGE = new Line("edge line", 15, 0, 0);

    /** Its lines along a stretch {@code length} long, its middle at nought. */
    List<Part> along(final double length) {
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
                parts.addAll(painted(line, at - line.wide() / 2, length));
            } else if (before.kind() == Strip.Kind.LANE) {
                parts.addAll(painted(EDGE, at - EDGE.wide(), length));
            } else if (after.kind() == Strip.Kind.LANE) {
                parts.addAll(painted(EDGE, at, length));
            }
        }
        return parts;
    }

    /** {@code line}, from {@code x} across, along a stretch {@code length} long: unbroken, or stroke by stroke. */
    private List<Part> painted(final Line line, final double x, final double length) {
        final List<Part> parts = new ArrayList<>();
        final double end = length / 2;
        if (line.stroke() <= 0) {
            parts.add(stroke(line, x, new Shapes.Run(-end, end)));
            return parts;
        }
        for (double z = -end; z < end; z += line.stroke() + line.gap()) {
            parts.add(stroke(line, x, new Shapes.Run(z, Math.min(z + line.stroke(), end))));
        }
        return parts;
    }

    private Part stroke(final Line line, final double x, final Shapes.Run run) {
        final double x1 = x + line.wide();
        final double y0 = level + section.top(x);
        final double y1 = level + section.top(x1);
        return new Part(new Elements.Marking(), new Shapes().solid(line.name(), Role.DRESSING, Material.PAINTED, run,
                x, y0, x1, y1, y0 + THICK, y1 + THICK).tinted(WHITE));
    }
}

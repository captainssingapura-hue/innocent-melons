package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A road's strips along a run of it, each a solid standing on what is level under it, the ground or a bridge's deck,
 * {@code level} high: a lane or a shoulder of asphalt, its top
 * falling as its section falls, parted at the road's line where it lies across it so its crown is there; a kerb of
 * precast concrete, {@link #KERB} over the pavement beside it, its top level; a barrier of concrete, its foot as wide
 * as its strip and its top {@link #CAP} wide over its middle, {@link #BARRIER} over the pavement there.
 *
 * @param section the road's section
 * @param level   how high what it stands on is
 */
record Pieces(Section section, double level) {

    /** How high a kerb stands over the pavement beside it; a barrier; how wide a barrier's top is. */
    static final double KERB = 15;
    static final double BARRIER = 81;
    static final double CAP = 15;

    /** Its strips along {@code run}. */
    List<Part> along(final Shapes.Run run) {
        final Shapes shapes = new Shapes();
        final List<Part> parts = new ArrayList<>();
        final List<Strip> strips = section.strips();
        for (int k = 0; k < strips.size(); k++) {
            final Strip strip = strips.get(k);
            final double x0 = section.edge(k);
            final double x1 = section.edge(k + 1);
            switch (strip.kind()) {
                case LANE, SHOULDER -> {
                    final boolean lane = strip.kind() == Strip.Kind.LANE;
                    if (x0 < 0 && x1 > 0) {
                        parts.add(paved(lane, run, x0, 0));
                        parts.add(paved(lane, run, 0, x1));
                    } else {
                        parts.add(paved(lane, run, x0, x1));
                    }
                }
                case KERB -> {
                    final double top = top(Math.abs(x0) < Math.abs(x1) ? x0 : x1) + KERB;
                    parts.add(new Part(new Elements.Kerb(), shapes.solid("kerb", Role.STRUCTURE, Material.PRECAST,
                            run, x0, level, x1, level, top, top)));
                }
                case BARRIER -> {
                    final double middle = (x0 + x1) / 2;
                    final double top = top(middle) + BARRIER;
                    parts.add(new Part(new Elements.Barrier(), shapes.solid("barrier", Role.STRUCTURE,
                            Material.CONCRETE, run, x0, level, x1, level, middle - CAP / 2, top, middle + CAP / 2,
                            top)));
                }
            }
        }
        return parts;
    }

    /** A lane, or a shoulder, from {@code x0} across to {@code x1}, along {@code run}. */
    private Part paved(final boolean lane, final Shapes.Run run, final double x0, final double x1) {
        return new Part(lane ? new Elements.TrafficLane() : new Elements.Shoulder(), new Shapes().solid(lane ? "lane"
                : "shoulder", Role.STRUCTURE, Material.ASPHALT, run, x0, level, x1, level, top(x0),
                top(x1)));
    }

    /** How high its pavement's top is {@code x} across from its line, standing on what is {@link #level} high. */
    private double top(final double x) {
        return level + section.top(x);
    }
}

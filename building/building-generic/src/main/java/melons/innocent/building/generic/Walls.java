package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A storey's wall, of concrete, cut round its openings so every piece of it is a box: piers between them, full height;
 * under each window its sill wall, over each opening its lintel; and in each opening what fills it, a window's pane
 * of glass in its middle in a frame of metal, or a door. A wall runs across, its thickness along, or along, its
 * thickness across. In centimetres.
 */
final class Walls {

    /** How thick a window's frame is, each way, and how thick its glass, and a door. */
    private static final double FRAME = 5;
    private static final double GLASS = 2;
    private static final double DOOR = 5;

    /**
     * An opening in a wall: where its middle is along the wall, how wide it is, how high its sill and its head are
     * over the storey's floor, and whether it is the door.
     *
     * @param middle where its middle is, along the wall
     * @param wide   how wide it is
     * @param sill   how high its sill is over the floor; nothing for a door
     * @param head   how high its head is
     * @param door   whether it is a door rather than a window
     */
    record Opening(double middle, double wide, double sill, double head, boolean door) {
    }

    private final Timbers boxes = Timbers.INSTANCE;

    /**
     * The wall from {@code from} to {@code to} along its run, {@code t0} to {@code t1} through it, from
     * {@code floor} to {@code top}, running across if {@code across}, else along; cut round {@code openings}.
     */
    List<Part> wall(final String name, final boolean across, final double from, final double to, final double t0,
                    final double t1, final double floor, final double top, final List<Opening> openings) {
        final List<Part> parts = new ArrayList<>();
        double cursor = from;
        for (final Opening o : openings.stream().sorted(Comparator.comparingDouble(Opening::middle)).toList()) {
            final double left = o.middle() - o.wide() / 2;
            final double right = o.middle() + o.wide() / 2;
            wall(parts, name + " pier", across, cursor, left, t0, t1, floor, top);
            wall(parts, name + " under a window", across, left, right, t0, t1, floor, floor + o.sill());
            wall(parts, name + " lintel", across, left, right, t0, t1, floor + o.head(), top);
            filled(parts, name, across, o, t0, t1, floor);
            cursor = right;
        }
        wall(parts, name + " pier", across, cursor, to, t0, t1, floor, top);
        return parts;
    }

    /** A piece of wall from {@code a0} to {@code a1} along its run, {@code y0} to {@code y1} up, if any of it. */
    private void wall(final List<Part> parts, final String name, final boolean across, final double a0,
                      final double a1, final double t0, final double t1, final double y0, final double y1) {
        if (a1 - a0 > 1 && y1 - y0 > 1) {
            parts.add(new Part(new Elements.BearingWall(), box(name, Role.WALL, Material.CONCRETE, across, a0, a1,
                    t0, t1, y0, y1)));
        }
    }

    /** What fills opening {@code o}: a door, or a window's pane in its frame, in the middle of the wall. */
    private void filled(final List<Part> parts, final String name, final boolean across, final Opening o,
                        final double t0, final double t1, final double floor) {
        final double left = o.middle() - o.wide() / 2;
        final double right = o.middle() + o.wide() / 2;
        final double mid = (t0 + t1) / 2;
        final double low = floor + o.sill();
        final double high = floor + o.head();
        if (o.door()) {
            parts.add(new Part(new Elements.Door(), box(name + " door", Role.DRESSING, Material.PAINTED, across, left,
                    right, mid - DOOR / 2, mid + DOOR / 2, low, high)));
            return;
        }
        parts.add(new Part(new Elements.Window(), box(name + " pane", Role.GLAZING, Material.GLASS, across,
                left + FRAME, right - FRAME, mid - GLASS / 2, mid + GLASS / 2, low + FRAME, high - FRAME)));
        final double f0 = mid - FRAME;
        final double f1 = mid + FRAME;
        for (final double[] bar : new double[][] {{left, right, low, low + FRAME}, {left, right, high - FRAME, high},
            {left, left + FRAME, low + FRAME, high - FRAME}, {right - FRAME, right, low + FRAME, high - FRAME}}) {
            parts.add(new Part(new Elements.Window(), box(name + " frame", Role.DRESSING, Material.METAL, across,
                    bar[0], bar[1], f0, f1, bar[2], bar[3])));
        }
    }

    /** A box from {@code a0} to {@code a1} along a run across or along, {@code t0} to {@code t1} through it. */
    Solid box(final String name, final Role role, final Material material, final boolean across, final double a0,
              final double a1, final double t0, final double t1, final double y0, final double y1) {
        return across ? boxes.block(name, role, material, a0, a1, t0, t1, y0, y1)
                : boxes.block(name, role, material, t0, t1, a0, a1, y0, y1);
    }
}

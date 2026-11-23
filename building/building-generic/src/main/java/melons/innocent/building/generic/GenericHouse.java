package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.HouseSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone builds a house today, no civilization's way in particular: a box of concrete. On a slab a little wider
 * than it, its walls 20 cm thick, its floors 20 cm slabs, storey on storey; its roof a flat concrete slab overhanging
 * its walls 30 cm all round, a parapet 90 cm high round its edge. Across its front on each storey so many windows,
 * evenly spaced, and as many across its back; one on each side, if it is 5 m deep or more; on the ground storey the
 * front window nearest its middle a door. Each storey a room, for living in; a stair up each storey along its left
 * wall, from the back. Its front toward along.
 */
public final class GenericHouse
        implements ThingImpl<Buildings.House, Civilization, CommonEras.Contemporary, HouseSpec<Civilization,
        CommonEras.Contemporary>> {

    static final double WALL = 20;
    static final double SLAB = 20;
    static final double OVERHANG = 30;
    static final double PARAPET = 90;
    static final double COPING = 15;
    static final double WINDOW = 120;
    static final double SILL = 90;
    static final double HEAD = 210;
    static final double DOOR = 100;
    static final double DOOR_HEAD = 220;
    static final double FLIGHT = 100;

    private final Timbers boxes = Timbers.INSTANCE;
    private final Walls walls = new Walls();

    @Override
    public Buildings.House thing() {
        return new Buildings.House();
    }

    @Override
    public Civilization civilization() {
        return Civilization.ROOT;
    }

    @Override
    public CommonEras.Contemporary era() {
        return new CommonEras.Contemporary();
    }

    @Override
    public Structure build(final HouseSpec<Civilization, CommonEras.Contemporary> s) {
        final double a = s.wide() / 2;
        final double d = s.deep() / 2;
        final double h = s.storey();
        final List<Part> parts = new ArrayList<>();
        final List<Structure.Room> rooms = new ArrayList<>();
        final List<Structure.Stair> stairs = new ArrayList<>();
        parts.add(new Part(new Elements.Floor(), boxes.block("ground slab", Role.BASE, Material.CONCRETE,
                -a - OVERHANG, a + OVERHANG, -d - OVERHANG, d + OVERHANG, -SLAB, 0)));
        double door = 0;
        for (int k = 0; k < s.storeys(); k++) {
            final double floor = k * h;
            final double top = (k + 1) * h - SLAB;
            final List<Walls.Opening> front = row(s, k == 0);
            door = front.stream().filter(Walls.Opening::door).mapToDouble(Walls.Opening::middle).findFirst()
                    .orElse(door);
            parts.addAll(walls.wall("front wall", true, -a, a, d - WALL, d, floor, top, front));
            parts.addAll(walls.wall("back wall", true, -a, a, -d, -d + WALL, floor, top, row(s, false)));
            final List<Walls.Opening> side = s.deep() >= 500 ? List.of(new Walls.Opening(0, WINDOW, SILL, HEAD,
                    false)) : List.of();
            parts.addAll(walls.wall("left wall", false, -d + WALL, d - WALL, -a, -a + WALL, floor, top, side));
            parts.addAll(walls.wall("right wall", false, -d + WALL, d - WALL, a - WALL, a, floor, top, side));
            rooms.add(new Structure.Room(new Spaces.Room(), new Uses.Homes(), List.of(new Vec2(-a + WALL,
                    -d + WALL), new Vec2(a - WALL, -d + WALL), new Vec2(a - WALL, d - WALL), new Vec2(-a + WALL,
                    d - WALL)), floor));
            if (k + 1 < s.storeys()) {
                parts.add(new Part(new Elements.Floor(), boxes.block("floor slab", Role.STRUCTURE, Material.CONCRETE,
                        -a, a, -d, d, top, top + SLAB)));
                stairs.add(stair(parts, a, d, floor, floor + h));
            }
        }
        roofed(parts, a, d, s.storeys() * h);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), rooms,
                List.of(new Structure.WayIn(door, d, 0)), stairs);
    }

    /** The openings across a front or back, as many windows as asked, evenly; on the ground storey's front, a door. */
    private List<Walls.Opening> row(final HouseSpec<?, ?> s, final boolean doorway) {
        final List<Walls.Opening> openings = new ArrayList<>();
        final double each = s.wide() / s.windows();
        final int middle = s.windows() / 2;
        for (int i = 0; i < s.windows(); i++) {
            final double at = -s.wide() / 2 + (i + 0.5) * each;
            openings.add(doorway && i == middle ? new Walls.Opening(at, DOOR, 0, DOOR_HEAD, true)
                    : new Walls.Opening(at, Math.min(WINDOW, each - 2 * WALL), SILL, HEAD, false));
        }
        return openings;
    }

    /** A stair up along the left wall from the back, from {@code foot} to {@code head}, added; it as a stair. */
    private Structure.Stair stair(final List<Part> parts, final double a, final double d, final double foot,
                                  final double head) {
        final double x0 = -a + WALL;
        final double z0 = -d + WALL + 20;
        final double z1 = Math.min(d - WALL - 20, z0 + (head - foot) * 1.6);
        parts.add(new Part(new Elements.Steps(), boxes.board("stair", Role.STRUCTURE, Material.CONCRETE, x0,
                x0 + FLIGHT, z0, z1, foot + 1, head, SLAB)));
        return new Structure.Stair(new Structure.WayIn(x0 + FLIGHT / 2, z0, foot),
                new Structure.WayIn(x0 + FLIGHT / 2, z1, head));
    }

    /** The roof at {@code eaves}: a flat slab overhanging the walls all round, a parapet round its edge. */
    private void roofed(final List<Part> parts, final double a, final double d, final double eaves) {
        final double x = a + OVERHANG;
        final double z = d + OVERHANG;
        parts.add(new Part(new Elements.Roof(), boxes.block("roof slab", Role.ROOF, Material.CONCRETE, -x, x, -z, z,
                eaves - SLAB, eaves)));
        final double top = eaves + PARAPET;
        parts.add(new Part(new Elements.Parapet(), boxes.block("front parapet", Role.DRESSING, Material.CONCRETE, -x,
                x, z - COPING, z, eaves, top)));
        parts.add(new Part(new Elements.Parapet(), boxes.block("back parapet", Role.DRESSING, Material.CONCRETE, -x,
                x, -z, -z + COPING, eaves, top)));
        parts.add(new Part(new Elements.Parapet(), boxes.block("left parapet", Role.DRESSING, Material.CONCRETE, -x,
                -x + COPING, -z + COPING, z - COPING, eaves, top)));
        parts.add(new Part(new Elements.Parapet(), boxes.block("right parapet", Role.DRESSING, Material.CONCRETE,
                x - COPING, x, -z + COPING, z - COPING, eaves, top)));
    }
}

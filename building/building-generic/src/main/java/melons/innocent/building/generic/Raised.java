package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A floor plan raised into a building's bare structure, of concrete: on a ground slab under its slabs, storey on
 * storey, the first {@code ground} high and the rest {@code typical}, each storey's shear walls standing from its floor
 * to the underside of the slab over it, and over each storey its slabs, the topmost its roof. A wall square to the
 * plan or not, every one is a box of its own eight corners, so it stands along its line, so thick. Bare: no doors,
 * no windows, no rooms; only what holds it up.
 *
 * @param slab how thick every slab is, in centimetres
 */
public record Raised(double slab) {

    /** The bare structure of {@code plan}, {@code storeys} high, the first {@code ground}, the rest {@code typical}. */
    public Structure of(final FloorPlan plan, final int storeys, final double ground, final double typical) {
        final List<Part> parts = new ArrayList<>();
        for (final FloorPlan.Slab s : plan.slabs()) {
            parts.add(new Part(new Elements.Floor(), slab("ground slab", Role.BASE, s, -slab, 0)));
        }
        double floor = 0;
        for (int k = 0; k < storeys; k++) {
            final double top = floor + (k == 0 ? ground : typical);
            for (final FloorPlan.Wall wall : plan.walls()) {
                parts.add(new Part(new Elements.BearingWall(), wall(wall, floor, top - slab)));
            }
            final boolean roof = k + 1 == storeys;
            for (final FloorPlan.Slab s : plan.slabs()) {
                parts.add(new Part(roof ? new Elements.Roof() : new Elements.Floor(), slab(roof ? "roof slab"
                        : "floor slab", roof ? Role.ROOF : Role.STRUCTURE, s, top - slab, top)));
            }
            floor = top;
        }
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(), List.of());
    }

    /** {@code s} as a slab of concrete from {@code y0} to {@code y1}. */
    private Solid slab(final String name, final Role role, final FloorPlan.Slab s, final double y0, final double y1) {
        return Timbers.INSTANCE.block(name, role, Material.CONCRETE, s.x0(), s.x1(), s.z0(), s.z1(), y0, y1);
    }

    /**
     * {@code w} as a box of concrete from {@code y0} to {@code y1}: its corners along its line from its beginning to
     * its end (bit 0), across it from one face to the other (bit 1), and up (bit 2).
     */
    private Solid wall(final FloorPlan.Wall w, final double y0, final double y1) {
        final double ux = (w.x1() - w.x0()) / w.length();
        final double uz = (w.z1() - w.z0()) / w.length();
        final double nx = -uz * w.thick() / 2;
        final double nz = ux * w.thick() / 2;
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final double x = (k & 1) == 0 ? w.x0() : w.x1();
            final double z = (k & 1) == 0 ? w.z0() : w.z1();
            final double side = (k >> 1 & 1) == 0 ? -1 : 1;
            corners.add(new Point(x + side * nx, z + side * nz, (k >> 2 & 1) == 0 ? y0 : y1));
        }
        return new Solid(w.name(), Role.STRUCTURE, Material.CONCRETE, corners);
    }
}

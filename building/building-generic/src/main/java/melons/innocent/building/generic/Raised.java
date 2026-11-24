package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
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
 * A floor plan raised into a building: on a ground slab under its slabs, storey on storey, the first {@code ground}
 * high and the rest {@code typical}, each storey's shear walls standing from its floor to the underside of the slab
 * over it, of concrete, and over each storey its slabs, the topmost its roof; and on every storey but the lowest
 * {@code open}, left open as a void deck, its cover ({@link Covered}). A wall square to the plan or not, every one is a
 * box of its own eight corners ({@link Along}), so it stands along its line, so thick.
 *
 * @param slab    how thick every slab is, in centimetres
 * @param open    how many of the lowest storeys are left uncovered
 * @param covered how its cover is made
 */
public record Raised(double slab, int open, Covered covered) {

    /** Its slabs so thick, its first storey left open, its cover as flats commonly have it. */
    public Raised(final double slab) {
        this(slab, 1, new Covered());
    }

    /** {@code plan} raised {@code storeys} high, the first {@code ground}, the rest {@code typical}. */
    public Structure of(final FloorPlan plan, final int storeys, final double ground, final double typical) {
        final Along along = new Along();
        final List<Part> parts = new ArrayList<>();
        for (final FloorPlan.Slab s : plan.slabs()) {
            parts.add(new Part(new Elements.Floor(), slab("ground slab", Role.BASE, s, -slab, 0)));
        }
        double floor = 0;
        for (int k = 0; k < storeys; k++) {
            final double top = floor + (k == 0 ? ground : typical);
            for (final FloorPlan.Wall wall : plan.walls()) {
                parts.add(new Part(new Elements.BearingWall(), along.whole(wall.name(), Role.STRUCTURE,
                        Material.CONCRETE, wall, floor, top - slab)));
            }
            if (k >= open) {
                for (final FloorPlan.Cover cover : plan.covers()) {
                    parts.addAll(covered.parts(cover, floor, top - slab));
                }
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
}

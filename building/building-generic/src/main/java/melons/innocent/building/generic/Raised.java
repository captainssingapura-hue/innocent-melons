package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;
import melons.innocent.forms.Tint;

import java.util.ArrayList;
import java.util.List;

/**
 * A floor plan raised into a building: on a ground slab under its slabs, storey on storey, the first {@code ground}
 * high and the rest {@code typical}, each storey's shear walls standing from its floor to the underside of the slab
 * over it, of concrete, and over each storey its slabs, the topmost its roof; and on every storey but the lowest
 * {@code open}, left open as a void deck, its cover ({@link Covered}). A wall square to the plan or not, every one is a
 * box of its own eight corners ({@link Along}), so it stands along its line, so thick. Its cores' walls, round its
 * stairs and lifts, painted {@link #CORES} and its stairs {@link #STAIRS}, as the board marks its blocks' cores out,
 * so where they are, and the way in to them, shows from outside. Each floor finished on top in {@link #FLOORS}, so
 * it is told from the walls standing on it, painted or bare.
 *
 * @param slab    how thick every slab is, in centimetres
 * @param open    how many of the lowest storeys are left uncovered
 * @param covered how its cover is made
 */
public record Raised(double slab, int open, Covered covered) {

    /** What colour its cores' walls are painted, its stairs, and its floors' finish; how thick that is. */
    static final Tint CORES = new Tint("#b8583f");
    static final Tint STAIRS = new Tint("#e8c25a");
    static final Tint FLOORS = new Tint("#6f7378");
    static final double FINISH = 2;

    /** Its slabs so thick, its first storey left open, its cover as flats commonly have it, of {@code infill}. */
    public Raised(final double slab, final Material infill) {
        this(slab, 1, new Covered(infill));
    }

    /** Its slabs so thick, its first storey left open, its cover as flats commonly have it, of brick. */
    public Raised(final double slab) {
        this(slab, Material.BRICK);
    }

    /**
     * {@code plan} raised {@code storeys} high, the first {@code ground}, the rest {@code typical}; its stairs up each
     * storey to the next, their wells through every slab but the ground's and the roof, their rails on the top floor.
     */
    public Structure of(final FloorPlan plan, final int storeys, final double ground, final double typical) {
        final Along along = new Along();
        final List<Part> parts = new ArrayList<>();
        final List<Structure.Stair> stairs = new ArrayList<>();
        final List<FloorPlan.Slab> holed = new Wells(plan.stairs().stream().map(DogLeg::well).toList())
                .of(plan.slabs());
        for (final FloorPlan.Slab s : plan.slabs()) {
            parts.addAll(floor("ground slab", Role.BASE, s, -slab, 0));
        }
        double floor = 0;
        for (int k = 0; k < storeys; k++) {
            final double top = floor + (k == 0 ? ground : typical);
            for (final DogLeg stair : plan.stairs()) {
                final List<Part> flights = k + 1 < storeys ? stair.storey(floor, top - floor) : stair.top(floor);
                flights.forEach(p -> parts.add(new Part(p.element(), p.solid().tinted(STAIRS))));
                if (k + 1 < storeys) {
                    stairs.add(stair.stair(floor, top - floor));
                }
            }
            for (final FloorPlan.Wall wall : plan.walls()) {
                parts.add(new Part(new Elements.BearingWall(), along.whole(wall.name(), Role.STRUCTURE,
                        Material.CONCRETE, wall, floor, top - slab)));
            }
            for (final FloorPlan.Wall wall : plan.cores()) {
                parts.add(new Part(new Elements.BearingWall(), along.whole(wall.name(), Role.STRUCTURE,
                        Material.CONCRETE, wall, floor, top - slab).tinted(CORES)));
            }
            if (k >= open) {
                for (final FloorPlan.Cover cover : plan.covers()) {
                    parts.addAll(covered.parts(cover, floor, top - slab));
                }
            }
            final boolean roof = k + 1 == storeys;
            for (final FloorPlan.Slab s : roof ? plan.slabs() : holed) {
                if (roof) {
                    parts.add(new Part(new Elements.Roof(), slab("roof slab", Role.ROOF, s, top - slab, top)));
                } else {
                    parts.addAll(floor("floor slab", Role.STRUCTURE, s, top - slab, top));
                }
            }
            floor = top;
        }
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(), List.of(),
                stairs);
    }

    /**
     * {@code s} as a floor from {@code y0} to {@code y1}: a slab of concrete, and over it its finish, {@link #FINISH}
     * thick, in {@link #FLOORS}, its top the floor's.
     */
    private List<Part> floor(final String name, final Role role, final FloorPlan.Slab s, final double y0,
                             final double y1) {
        return List.of(new Part(new Elements.Floor(), slab(name, role, s, y0, y1 - FINISH)),
                new Part(new Elements.Floor(), slab("floor finish", role, s, y1 - FINISH, y1).tinted(FLOORS)));
    }

    /** {@code s} as a slab of concrete from {@code y0} to {@code y1}. */
    private Solid slab(final String name, final Role role, final FloorPlan.Slab s, final double y0, final double y1) {
        return Timbers.INSTANCE.block(name, role, Material.CONCRETE, s.x0(), s.x1(), s.z0(), s.z1(), y0, y1);
    }
}

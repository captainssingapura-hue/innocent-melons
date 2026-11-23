package melons.innocent.building.generic;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How a slab block of public housing was built in the modern era, as the corridor-access blocks of Singapore's housing
 * board were, as bare structure: its floor plan of shear walls, raised storey on storey ({@link Raised}). Along its
 * length, bays in turn: a core, so many flats, a core, and so on, a core at each end; a party wall between every two
 * bays, front to back, the end ones its gables; each core closed behind by a wall, a shaft for its stairs and lifts;
 * a spine wall along the whole of it between its flats and its corridor; each storey's slab over flats and corridor
 * alike. Covered, above its first storey, in lightweight brick ({@link Covered}): across its back each flat a window
 * as wide as it is, between its party walls, and behind each core a wall; its corridor an access balcony along its
 * front, guarded by a parapet half a storey high. Its first storey 3.6 m, open below as a void deck, the rest 2.8 m,
 * as the board's own guide gives them; its walls and slabs 20 cm, its cover 10 cm. Its front, the corridor's side,
 * toward along.
 */
public final class GenericSlabBlock
        implements ThingImpl<Buildings.SlabBlock, Civilization, CommonEras.Modern, SlabBlockSpec<Civilization,
        CommonEras.Modern>> {

    static final double WALL = 20;
    static final double SLAB = 20;
    static final double GROUND = 360;
    static final double TYPICAL = 280;
    static final double CORE_WIDE = 600;
    static final double CORE_DEEP = 700;
    static final double COVER = 10;

    @Override
    public Buildings.SlabBlock thing() {
        return new Buildings.SlabBlock();
    }

    @Override
    public Civilization civilization() {
        return Civilization.ROOT;
    }

    @Override
    public CommonEras.Modern era() {
        return new CommonEras.Modern();
    }

    @Override
    public Structure build(final SlabBlockSpec<Civilization, CommonEras.Modern> s) {
        return new Raised(SLAB).of(plan(s), s.storeys(), GROUND, TYPICAL);
    }

    /**
     * {@code s}'s floor plan: its bays' party walls, its cores' back walls, its spine wall and its slab; and its cover,
     * a window as wide as each flat across its back, the back closed behind each core, and a parapet along its access
     * balcony.
     */
    public FloorPlan plan(final SlabBlockSpec<?, ?> s) {
        final List<double[]> bays = new ArrayList<>();
        int left = s.flats();
        bays.add(new double[] {CORE_WIDE, 1});
        while (left > 0) {
            final int group = Math.min(left, s.perCore());
            for (int i = 0; i < group; i++) {
                bays.add(new double[] {s.flatWide(), 0});
            }
            bays.add(new double[] {CORE_WIDE, 1});
            left -= group;
        }
        final double length = bays.stream().mapToDouble(bay -> bay[0]).sum();
        final double deep = s.flatDeep();
        final List<FloorPlan.Wall> walls = new ArrayList<>();
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        double x = -length / 2;
        walls.add(new FloorPlan.Wall(x, -deep, x, 0, WALL, "gable"));
        for (int b = 0; b < bays.size(); b++) {
            final double next = x + bays.get(b)[0];
            final boolean core = bays.get(b)[1] == 1;
            if (core) {
                walls.add(new FloorPlan.Wall(x, -CORE_DEEP, next, -CORE_DEEP, WALL, "core's back wall"));
            }
            covers.add(new FloorPlan.Cover(new FloorPlan.Wall(x + WALL / 2, -deep + COVER / 2, next - WALL / 2,
                    -deep + COVER / 2, COVER, core ? "back wall behind a core" : "back wall"),
                    core ? FloorPlan.Kind.SOLID : FloorPlan.Kind.WINDOWED));
            walls.add(new FloorPlan.Wall(next, -deep, next, 0, WALL, b + 1 == bays.size() ? "gable" : "party wall"));
            x = next;
        }
        walls.add(new FloorPlan.Wall(-length / 2, 0, length / 2, 0, WALL, "spine wall"));
        covers.add(new FloorPlan.Cover(new FloorPlan.Wall(-length / 2, s.corridor() - COVER / 2, length / 2,
                s.corridor() - COVER / 2, COVER, "access balcony's parapet"), FloorPlan.Kind.PARAPET));
        return new FloorPlan(walls, List.of(new FloorPlan.Slab(-length / 2, -deep, length / 2, s.corridor())),
                covers);
    }
}

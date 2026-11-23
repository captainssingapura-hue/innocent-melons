package melons.innocent.building.generic;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.List;

/**
 * How a point block of public housing was built in the modern era, as Singapore's housing board built them, as bare
 * structure: its floor plan of shear walls, raised storey on storey ({@link Raised}). Square in plan; at its middle a
 * square core of stairs and lifts, closed all round by its four walls; from the middle of each of the core's sides a
 * party wall out to the block's edge, so four flats lie round the core, one to each corner, each reached from it
 * straight; each storey's slab over the whole square. Its first storey 3.6 m, open below as a void deck, the rest
 * 2.8 m, as the board's own guide gives them; its walls and slabs 20 cm.
 */
public final class GenericPointBlock
        implements ThingImpl<Buildings.PointBlock, Civilization, CommonEras.Modern, PointBlockSpec<Civilization,
        CommonEras.Modern>> {

    static final double WALL = 20;
    static final double SLAB = 20;
    static final double GROUND = 360;
    static final double TYPICAL = 280;

    @Override
    public Buildings.PointBlock thing() {
        return new Buildings.PointBlock();
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
    public Structure build(final PointBlockSpec<Civilization, CommonEras.Modern> s) {
        return new Raised(SLAB).of(plan(s), s.storeys(), GROUND, TYPICAL);
    }

    /** {@code s}'s floor plan: its core's four walls, its four party walls out from them, and its slab. */
    public FloorPlan plan(final PointBlockSpec<?, ?> s) {
        final double h = s.side() / 2;
        final double c = s.core() / 2;
        return new FloorPlan(List.of(
                new FloorPlan.Wall(-c, -c, c, -c, WALL, "core wall"),
                new FloorPlan.Wall(c, -c, c, c, WALL, "core wall"),
                new FloorPlan.Wall(c, c, -c, c, WALL, "core wall"),
                new FloorPlan.Wall(-c, c, -c, -c, WALL, "core wall"),
                new FloorPlan.Wall(c, 0, h, 0, WALL, "party wall"),
                new FloorPlan.Wall(-c, 0, -h, 0, WALL, "party wall"),
                new FloorPlan.Wall(0, c, 0, h, WALL, "party wall"),
                new FloorPlan.Wall(0, -c, 0, -h, WALL, "party wall")),
                List.of(new FloorPlan.Slab(-h, -h, h, h)));
    }
}

package melons.innocent.building.generic;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How a point block of public housing was built in the modern era, as Singapore's housing board built them: four
 * executive flats ({@link ExecutiveFlat}) round a lift lobby they share, raised storey on storey ({@link Raised}). At
 * its middle the lobby, square, walled all round in concrete but for a gap to each flat's porch, its timber door in
 * it, the lifts' shaft at its middle; west of it the first flat, its porch on the lobby's west wall, and the others
 * each a quarter turn on from the last, a pinwheel, the gaps between them open to the sky. Each storey's slabs under
 * the flats and the lobby; its structure the flats' pillars, the lobby's walls and the shaft. Its first storey 3.6 m,
 * open below as a void deck, the rest 2.8 m, as the board's own guide gives them; its slabs 20 cm.
 */
public final class GenericPointBlock
        implements ThingImpl<Buildings.PointBlock, Civilization, CommonEras.Modern, PointBlockSpec<Civilization,
        CommonEras.Modern>> {

    static final double SLAB = 20;
    static final double GROUND = 360;
    static final double TYPICAL = 280;
    /** How thick the lobby's walls are, and the shaft's; how big the shaft is, across and along. */
    static final double CORE_WALL = 30;
    static final double SHAFT_WALL = 20;
    static final double SHAFT_ACROSS = 400;
    static final double SHAFT_ALONG = 250;

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

    /**
     * {@code s}'s floor plan: each flat, turned to its quarter, and the lobby's wall that side; the shaft; the lobby's
     * slab. The first flat west of the lobby, its porch's face on the lobby's west wall, its corner nearest the lobby
     * level with the lobby's south side, so that the next flat, a quarter on, clears it.
     */
    public FloorPlan plan(final PointBlockSpec<?, ?> s) {
        final double c = s.core() / 2;
        final FloorPlan flat = new ExecutiveFlat().plan();
        final List<FloorPlan.Wall> walls = new ArrayList<>();
        final List<FloorPlan.Slab> slabs = new ArrayList<>();
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        for (int q = 0; q < 4; q++) {
            final FloorPlan turned = new Turned(q, -ExecutiveFlat.LOBBY - c, -c).plan(flat);
            walls.addAll(turned.walls());
            slabs.addAll(turned.slabs());
            covers.addAll(turned.covers());
            walls.addAll(new Turned(q, 0, 0).walls(lobbyWall(c)));
            covers.addAll(new Turned(q, 0, 0).covers(List.of(door(c))));
        }
        walls.addAll(shaft());
        slabs.add(new FloorPlan.Slab(-c, -c, c, c));
        return new FloorPlan(walls, slabs, covers);
    }

    /** The lobby's west wall, the lobby {@code c} each way from its middle: inside it, either side of the door. */
    private List<FloorPlan.Wall> lobbyWall(final double c) {
        final double x = -c + CORE_WALL / 2;
        final List<FloorPlan.Wall> wall = new ArrayList<>();
        final double door = ExecutiveFlat.DOOR_FROM - c;
        final double past = ExecutiveFlat.DOOR_TO - c;
        wall.add(new FloorPlan.Wall(x, -c, x, door, CORE_WALL, "lobby wall"));
        if (c - past > CORE_WALL) {
            wall.add(new FloorPlan.Wall(x, past, x, c, CORE_WALL, "lobby wall"));
        }
        return wall;
    }

    /**
     * The door in the lobby's west wall, the lobby {@code c} each way from its middle: the first flat's, from its
     * porch, as wide as the gap the wall leaves for it, in the wall's thickness.
     */
    private FloorPlan.Cover door(final double c) {
        final double x = -c + CORE_WALL / 2;
        return new FloorPlan.Cover(new FloorPlan.Wall(x, ExecutiveFlat.DOOR_FROM - c, x, ExecutiveFlat.DOOR_TO - c,
                CORE_WALL, "front door"), FloorPlan.Kind.DOOR);
    }

    /** The lifts' shaft, at the lobby's middle, closed all round. */
    private List<FloorPlan.Wall> shaft() {
        final double a = (SHAFT_ACROSS - SHAFT_WALL) / 2;
        final double b = (SHAFT_ALONG - SHAFT_WALL) / 2;
        return List.of(
                new FloorPlan.Wall(-a - SHAFT_WALL / 2, -b, a + SHAFT_WALL / 2, -b, SHAFT_WALL, "lift shaft"),
                new FloorPlan.Wall(-a - SHAFT_WALL / 2, b, a + SHAFT_WALL / 2, b, SHAFT_WALL, "lift shaft"),
                new FloorPlan.Wall(-a, -b + SHAFT_WALL / 2, -a, b - SHAFT_WALL / 2, SHAFT_WALL, "lift shaft"),
                new FloorPlan.Wall(a, -b + SHAFT_WALL / 2, a, b - SHAFT_WALL / 2, SHAFT_WALL, "lift shaft"));
    }
}

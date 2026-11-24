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
 * board were: its floor plan raised storey on storey ({@link Raised}). Along its length, bays in turn: a core, so many
 * flats, a core, and so on, a core at each end. Its flats stand on pillars only ({@link CorridorFlat}), every other one
 * mirrored, so two lie bathroom to bathroom and the pillars on the party line between them serve both. Each core a
 * shaft for its stairs and lifts, walled in concrete either side the whole depth of the block, and behind across it,
 * open on the corridor. Each storey's slab over flats and corridor alike. Covered, above its first storey, in
 * lightweight brick ({@link Covered}) between the pillars ({@link Infill}): each flat windowed across its back and
 * across its face on the corridor, a party wall on each party line; the back closed behind each core; and the
 * corridor an access balcony along its front, guarded by a parapet half a storey high. Its first storey 3.6 m, open
 * below as a void deck, the rest 2.8 m, as the board's own guide gives them; its cores' walls and its slabs 20 cm, its
 * cover 10 cm. Its front, the corridor's side, toward along.
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

    /** A bay of it, from one line across to the next: a core, or a flat, mirrored or not. */
    private record Bay(double x0, double x1, boolean core, boolean mirrored) {
    }

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
     * {@code s}'s floor plan: its flats' pillars and those on their party lines, its cores' walls, and its slab; and
     * its cover, between the pillars.
     */
    public FloorPlan plan(final SlabBlockSpec<?, ?> s) {
        final List<Bay> bays = bays(s);
        final double length = bays.getLast().x1() - bays.getFirst().x0();
        final double deep = s.flatDeep();
        final CorridorFlat flat = new CorridorFlat();
        final List<Box> pillars = new ArrayList<>();
        final List<FloorPlan.Wall> walls = new ArrayList<>();
        for (final Bay bay : bays) {
            if (bay.core()) {
                walls.addAll(core(bay, bays, deep));
            } else {
                pillars.addAll(flat.inside(bay.x0(), s.flatWide(), -deep, deep, bay.mirrored()));
            }
        }
        for (int k = 1; k < bays.size(); k++) {
            final Bay before = bays.get(k - 1);
            final Bay after = bays.get(k);
            if (!before.core() || !after.core()) {
                // a mirrored flat's right is its own left; the one after a line has its left on it
                final boolean leftHand = before.core() ? !after.mirrored() : before.mirrored();
                pillars.addAll(flat.line(after.x0(), -deep, deep, leftHand));
            }
        }
        // the cover fills between the cores' walls as it does between the pillars
        final List<Box> standing = new ArrayList<>(pillars);
        walls.forEach(w -> standing.add(box(w)));
        walls.addAll(pillars.stream().map(p -> p.wall("pillar")).toList());
        return new FloorPlan(walls, List.of(new FloorPlan.Slab(-length / 2, -deep, length / 2, s.corridor())),
                covers(bays, new Infill(standing), deep, s.corridor()));
    }

    /** What {@code wall}, square to the plan, stands on. */
    private Box box(final FloorPlan.Wall wall) {
        final double half = wall.thick() / 2;
        return wall.x0() == wall.x1()
                ? new Box(wall.x0() - half, Math.min(wall.z0(), wall.z1()), wall.x0() + half,
                Math.max(wall.z0(), wall.z1()))
                : new Box(Math.min(wall.x0(), wall.x1()), wall.z0() - half, Math.max(wall.x0(), wall.x1()),
                wall.z0() + half);
    }

    /** {@code s}'s bays, from one end to the other, its middle at nought: every other flat of a group mirrored. */
    private List<Bay> bays(final SlabBlockSpec<?, ?> s) {
        final int groups = (s.flats() + s.perCore() - 1) / s.perCore();
        final double length = s.flats() * s.flatWide() + (groups + 1) * CORE_WIDE;
        final List<Bay> bays = new ArrayList<>();
        double x = -length / 2;
        bays.add(new Bay(x, x += CORE_WIDE, true, false));
        int left = s.flats();
        while (left > 0) {
            final int group = Math.min(left, s.perCore());
            for (int i = 0; i < group; i++) {
                bays.add(new Bay(x, x += s.flatWide(), false, i % 2 == 1));
            }
            bays.add(new Bay(x, x += CORE_WIDE, true, false));
            left -= group;
        }
        return bays;
    }

    /**
     * {@code core}'s walls: either side the block's whole {@code deep}, flush with the block's end if it is at one,
     * else just inside the pillars on the line; and across it behind, {@link #CORE_DEEP} back.
     */
    private List<FloorPlan.Wall> core(final Bay core, final List<Bay> bays, final double deep) {
        final double first = core == bays.getFirst() ? WALL / 2 : CorridorFlat.PILLAR / 2 + WALL / 2;
        final double last = core == bays.getLast() ? WALL / 2 : CorridorFlat.PILLAR / 2 + WALL / 2;
        final double a = core.x0() + first;
        final double b = core.x1() - last;
        return List.of(new FloorPlan.Wall(a, -deep, a, 0, WALL, "core's side wall"),
                new FloorPlan.Wall(b, -deep, b, 0, WALL, "core's side wall"),
                new FloorPlan.Wall(a + WALL / 2, -CORE_DEEP, b - WALL / 2, -CORE_DEEP, WALL, "core's back wall"));
    }

    /**
     * The cover of {@code bays}, between the pillars {@code infill} fills between: each bay's back, inside it, windowed
     * behind a flat and solid behind a core; each flat's face on the corridor, windowed; a party wall on each line a
     * flat is on; and the access balcony's parapet along the corridor's edge.
     */
    private List<FloorPlan.Cover> covers(final List<Bay> bays, final Infill infill, final double deep,
                                         final double corridor) {
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        for (int k = 0; k < bays.size(); k++) {
            final Bay bay = bays.get(k);
            covers.addAll(infill.along(new Infill.Side(bay.x0(), -deep, bay.x1(), -deep, bay.core()
                    ? FloorPlan.Kind.SOLID : FloorPlan.Kind.WINDOWED, bay.core() ? "back wall behind a core"
                    : "back wall"), COVER / 2, COVER));
            if (!bay.core()) {
                covers.addAll(infill.along(new Infill.Side(bay.x1(), 0, bay.x0(), 0, FloorPlan.Kind.WINDOWED,
                        "front wall"), COVER / 2, COVER));
            }
            if (k > 0 && (!bay.core() || !bays.get(k - 1).core())) {
                covers.addAll(infill.along(new Infill.Side(bay.x0(), -deep, bay.x0(), 0, FloorPlan.Kind.SOLID,
                        "party wall"), 0, COVER));
            }
        }
        final double length = bays.getLast().x1() - bays.getFirst().x0();
        covers.add(new FloorPlan.Cover(new FloorPlan.Wall(-length / 2, corridor - COVER / 2, length / 2,
                corridor - COVER / 2, COVER, "access balcony's parapet"), FloorPlan.Kind.PARAPET));
        return covers;
    }
}

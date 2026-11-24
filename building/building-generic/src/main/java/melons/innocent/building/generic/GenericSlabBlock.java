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
 * bays of its structure, a core, and so on, a core at each end. How many bays, and how many cores, worked out from how
 * long it is asked to be: no more than {@link #RUN} bays between two cores, as few cores as that lets be, and as many
 * bays as bring each nearest the board's 10.2 m, none narrower than {@link #NARROWEST} nor wider than {@link #WIDEST},
 * the bays shared among the runs between the cores as evenly as they go. Each bay stands on pillars only
 * ({@link SlabBay}), every other one of a run mirrored, so two lie bathroom to bathroom and the pillars on the line
 * between them serve both. Each core a shaft for its stairs and lifts, walled in concrete either side the whole depth
 * of the block, and behind across it, open on the corridor. Each storey's slab over bays and corridor alike. Covered,
 * above its first storey, in what it is asked to be filled in with ({@link Covered}), brick, precast panels, concrete
 * or stone, between the pillars ({@link Infill}): each bay
 * windowed across its back and across its face on the corridor, a party wall on each line between bays; the back
 * closed behind each core; and the corridor an access balcony along its front, guarded by a parapet half a storey
 * high. Its first storey open below as a void deck; its storeys as high as asked; its cores' walls and its slabs
 * 20 cm, its cover 10 cm. Its front, the corridor's side, toward along.
 */
public final class GenericSlabBlock
        implements ThingImpl<Buildings.SlabBlock, Civilization, CommonEras.Modern, SlabBlockSpec<Civilization,
        CommonEras.Modern>> {

    static final double WALL = 20;
    static final double SLAB = 20;
    static final double CORE_WIDE = 600;
    static final double CORE_DEEP = 700;
    static final double COVER = 10;
    /** How wide the board's bay is, and how much narrower and wider one may be. */
    static final double BAY = 1_020;
    static final double NARROWEST = 750;
    static final double WIDEST = 1_250;
    /** How many bays at most between two cores. */
    static final int RUN = 6;

    /** A bay of it, from one line across to the next: a core, or a bay of its structure, mirrored or not. */
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
        final Finished finished = new Finished(s.finish());
        return finished.of(new Raised(SLAB, finished.infill()).of(plan(s), s.storeys(), s.ground(), s.storey()));
    }

    /**
     * {@code s}'s floor plan: its bays' pillars and those on the lines between them, its cores' walls, and its slab;
     * and its cover, between the pillars.
     */
    public FloorPlan plan(final SlabBlockSpec<?, ?> s) {
        final List<Bay> bays = bays(s.length());
        final double deep = s.deep();
        final SlabBay bay = new SlabBay();
        final List<Box> pillars = new ArrayList<>();
        final List<FloorPlan.Wall> walls = new ArrayList<>();
        for (final Bay b : bays) {
            if (b.core()) {
                walls.addAll(core(b, bays, deep));
            } else {
                pillars.addAll(bay.inside(b.x0(), b.x1() - b.x0(), -deep, deep, b.mirrored()));
            }
        }
        for (int k = 1; k < bays.size(); k++) {
            final Bay before = bays.get(k - 1);
            final Bay after = bays.get(k);
            if (!before.core() || !after.core()) {
                // a mirrored bay's right is its own left; the one after a line has its left on it
                final boolean leftHand = before.core() ? !after.mirrored() : before.mirrored();
                pillars.addAll(bay.line(after.x0(), -deep, deep, leftHand));
            }
        }
        // the cover fills between the cores' walls as it does between the pillars
        final List<Box> standing = new ArrayList<>(pillars);
        walls.forEach(w -> standing.add(box(w)));
        walls.addAll(pillars.stream().map(p -> p.wall("pillar")).toList());
        return new FloorPlan(walls, List.of(new FloorPlan.Slab(-s.length() / 2, -deep, s.length() / 2,
                s.corridor())), covers(bays, new Infill(standing), deep, s.corridor()));
    }

    /**
     * How many bays a block {@code length} long has: of those {@link #RUN} or fewer a run lets be, between
     * {@link #NARROWEST} and {@link #WIDEST} wide, as many as bring each nearest {@link #BAY}.
     */
    int count(final double length) {
        int best = 0;
        for (int n = 1; n * NARROWEST <= length; n++) {
            final double wide = wide(length, n);
            if (wide >= NARROWEST && wide <= WIDEST && (best == 0 || Math.abs(wide - BAY)
                    < Math.abs(wide(length, best) - BAY))) {
                best = n;
            }
        }
        if (best == 0) {
            throw new IllegalArgumentException("no bays " + NARROWEST + " to " + WIDEST + " wide fill " + length);
        }
        return best;
    }

    /** How wide each of {@code n} bays is, a block {@code length} long, its cores between them. */
    private double wide(final double length, final int n) {
        return (length - (runs(n) + 1) * CORE_WIDE) / n;
    }

    /** How many runs {@code n} bays make, no more than {@link #RUN} to each. */
    private int runs(final int n) {
        return (n + RUN - 1) / RUN;
    }

    /** The bays of a block {@code length} long, from one end to the other, its middle at nought. */
    private List<Bay> bays(final double length) {
        final int n = count(length);
        final int runs = runs(n);
        final double wide = wide(length, n);
        final List<Bay> bays = new ArrayList<>();
        double x = -length / 2;
        bays.add(new Bay(x, x += CORE_WIDE, true, false));
        for (int r = 0; r < runs; r++) {
            final int run = n / runs + (r < n % runs ? 1 : 0);
            for (int i = 0; i < run; i++) {
                bays.add(new Bay(x, x += wide, false, i % 2 == 1));
            }
            bays.add(new Bay(x, x += CORE_WIDE, true, false));
        }
        return bays;
    }

    /**
     * {@code core}'s walls: either side the block's whole {@code deep}, flush with the block's end if it is at one,
     * else just inside the pillars on the line; and across it behind, {@link #CORE_DEEP} back.
     */
    private List<FloorPlan.Wall> core(final Bay core, final List<Bay> bays, final double deep) {
        final double first = core == bays.getFirst() ? WALL / 2 : SlabBay.PILLAR / 2 + WALL / 2;
        final double last = core == bays.getLast() ? WALL / 2 : SlabBay.PILLAR / 2 + WALL / 2;
        final double a = core.x0() + first;
        final double b = core.x1() - last;
        return List.of(new FloorPlan.Wall(a, -deep, a, 0, WALL, "core's side wall"),
                new FloorPlan.Wall(b, -deep, b, 0, WALL, "core's side wall"),
                new FloorPlan.Wall(a + WALL / 2, -CORE_DEEP, b - WALL / 2, -CORE_DEEP, WALL, "core's back wall"));
    }

    /**
     * The cover of {@code bays}, between the pillars {@code infill} fills between: each bay's back, inside it, windowed
     * behind a bay of the structure and solid behind a core; each such bay's face on the corridor, windowed; a party
     * wall on each line next to one; and the access balcony's parapet along the corridor's edge.
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

    /** What {@code wall}, square to the plan, stands on. */
    private Box box(final FloorPlan.Wall wall) {
        final double half = wall.thick() / 2;
        return wall.x0() == wall.x1()
                ? new Box(wall.x0() - half, Math.min(wall.z0(), wall.z1()), wall.x0() + half,
                Math.max(wall.z0(), wall.z1()))
                : new Box(Math.min(wall.x0(), wall.x1()), wall.z0() - half, Math.max(wall.x0(), wall.x1()),
                wall.z0() + half);
    }
}

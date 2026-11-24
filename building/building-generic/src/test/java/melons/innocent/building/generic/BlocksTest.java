package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.building.spec.Walling;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.forms.Tint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The slab block and the point block, their structure and their cover: nothing wrong with either; each storey its
 * plan's walls, its slabs over them, the topmost its roof; as high as its storeys, the first 3.6 m and the rest 2.8 m;
 * the slab block as long as asked, its bays and cores worked out from it, a corner home at each end; the point block
 * four executive flats on their pillars, round their lobby, clear of one another; covered above the void deck in brick,
 * the slab block's windows as wide as each flat's back, the access balcony guarded half a storey high; and blocks too
 * big or too small refused.
 */
class BlocksTest {

    private final Tint frames = new Tint("#3c3f44");
    private final Tint glass = new Tint("#6f8fa3");
    private final Finish brick = new Finish(Walling.BRICK, Optional.empty(), frames, glass);
    private final SlabBlockSpec<Civilization, CommonEras.Modern> slab = new SlabBlockSpec<>(13_440, 1_205, 180, 12,
            360, 280, brick);
    private final PointBlockSpec<Civilization, CommonEras.Modern> point = new PointBlockSpec<>(20, 900);

    @Test
    void theSlabBlockItsBaysItsCoresItsStoreys() {
        final Structure block = new GenericSlabBlock().build(slab);
        assertEquals(List.of(), block.problems());
        final FloorPlan plan = new GenericSlabBlock().plan(slab);
        // as long as twelve of the board's bays and two cores: a corner, two bays, a core, six bays, a core, two bays
        // and a corner. Each bay nine pillars of its own; on each run's lines, its ends' too, bays mirrored in turn,
        // two pillars and three by turns; each core three walls of its own, and two by its stair: along its side, and
        // across its lifts
        assertEquals(12, new GenericSlabBlock().count(slab.length()));
        assertEquals(12 * 9 + 2 * (2 * 2 + 2 * 3) + (4 * 2 + 3 * 3), plan.walls().size());
        assertEquals(2 * (3 + 2), plan.cores().size());
        final List<FloorPlan.Wall> pillars = plan.walls().stream().filter(w -> w.name().equals("pillar")).toList();
        assertTrue(pillars.stream().allMatch(w -> w.thick() == SlabBay.PILLAR && w.length() <= 180),
                "blades 30 cm thick, none longer than 1.8 m");
        assertEquals(12 * (plan.walls().size() + plan.cores().size()),
                block.solids(new Elements.BearingWall()).size());
        assertEquals(1 + 11 * 7, block.solids(new Elements.Floor()).size(),
                "a slab over each storey but the top, in seven pieces round the cores' two wells");
        assertEquals(1, block.solids(new Elements.Roof()).size());
        final double[] size = size(block.solids());
        assertEquals(13_440, size[0], 1e-6, "as long as asked, its end pillars flush");
        assertEquals(1_205, size[1], 1e-6, "as wide as asked, its bays and its corridor");
        assertEquals(360 + 11 * 280 + GenericSlabBlock.SLAB, size[2], 1e-6, "from under the ground slab to the roof");
        // wider and deeper than the board's: eleven bays 10.45 m wide nearer its 10.2 m than ten 11.5 m wide, in runs
        // of three, five and three, the last of five unmirrored; the same pillars, as big
        final SlabBlockSpec<Civilization, CommonEras.Modern> wider = new SlabBlockSpec<>(12_700, 1_380, 180, 6, 450,
                320, brick);
        assertEquals(11, new GenericSlabBlock().count(wider.length()));
        final List<FloorPlan.Wall> stretched = new GenericSlabBlock().plan(wider).walls();
        assertEquals(11 * 9 + 2 * (2 * 2 + 2 * 3) + (3 * 2 + 3 * 3), stretched.size());
        assertTrue(stretched.stream().filter(w -> w.name().equals("pillar"))
                .allMatch(w -> Math.abs(w.thick() - 30) < 1e-9), "pillars stretched apart, none bigger");
        assertEquals(450 + 5 * 320 + GenericSlabBlock.SLAB, size(new GenericSlabBlock().build(wider).solids())[2],
                1e-6, "its storeys as high as asked");
    }

    @Test
    void aSlabBlockAnyLengthItsBaysWorkedOut() {
        final GenericSlabBlock block = new GenericSlabBlock();
        for (double length = 2_700; length <= 25_000; length += 10) {
            final SlabBlockSpec<Civilization, CommonEras.Modern> s = new SlabBlockSpec<>(length, 1_205, 180, 4, 360,
                    280, brick);
            final int n = block.count(length);
            final int cores = block.cores(n);
            assertEquals((n + GenericSlabBlock.RUN - 1) / GenericSlabBlock.RUN, cores, "a core every six bays or so");
            final double wide = (length - cores * GenericSlabBlock.CORE_WIDE) / n;
            assertTrue(wide >= GenericSlabBlock.NARROWEST - 1e-9 && wide <= GenericSlabBlock.WIDEST + 1e-9,
                    length + ": " + n + " bays " + wide + " wide");
            final FloorPlan.Slab floor = block.plan(s).slabs().getFirst();
            assertEquals(length, floor.x1() - floor.x0(), 1e-6, "as long as asked");
        }
        assertEquals(2, block.count(2_700), "the shortest, two corner homes 10.5 m wide and a core");
        // too short, too high, too deep behind its corridor, its first storey too low, its others too high, unfilled
        final Object[][] refused = {{2_600, 1_205, 12, 360, 280, brick},
            {13_440, 1_205, 41, 360, 280, brick}, {13_440, 1_900, 12, 360, 280, brick},
            {13_440, 1_205, 12, 250, 280, brick}, {13_440, 1_205, 12, 360, 400, brick},
            {13_440, 1_205, 12, 360, 280, null}};
        for (final Object[] r : refused) {
            assertThrows(IllegalArgumentException.class, () -> new SlabBlockSpec<>(((Number) r[0]).doubleValue(),
                    ((Number) r[1]).doubleValue(), 180, (int) r[2], ((Number) r[3]).doubleValue(),
                    ((Number) r[4]).doubleValue(), (Finish) r[5]));
        }
    }

    @Test
    void thePointBlockFourExecutiveFlatsRoundTheirLobby() {
        final Structure block = new GenericPointBlock().build(point);
        assertEquals(List.of(), block.problems());
        final FloorPlan plan = new GenericPointBlock().plan(point);
        final List<FloorPlan.Wall> pillars = new ExecutiveFlat().walls();
        assertEquals(19, pillars.size(), "as the board's plan has them");
        assertTrue(pillars.stream().allMatch(w -> w.thick() == ExecutiveFlat.PILLAR && w.length() <= 180),
                "blades 30 cm thick, none longer than 1.8 m");
        // each flat's pillars; and its core's: the lobby's wall each side, in two, either side of its door, the shaft's
        // four, and the wall behind the stair
        assertEquals(4 * pillars.size(), plan.walls().size());
        assertEquals(4 * 2 + 4 + 1, plan.cores().size());
        assertEquals(20 * (plan.walls().size() + plan.cores().size()),
                block.solids(new Elements.BearingWall()).size());
        final List<FloorPlan.Slab> slabs = plan.slabs();
        for (int i = 0; i < slabs.size(); i++) {
            for (int j = i + 1; j < slabs.size(); j++) {
                assertTrue(apart(slabs.get(i), slabs.get(j)), "the flats clear one another and the lobby");
            }
        }
        final double[] size = size(block.solids());
        final double reach = 2 * (ExecutiveFlat.LOBBY + 450);
        assertEquals(reach, size[0], 1e-6, "a flat each side, the lobby between");
        assertEquals(reach, size[1], 1e-6);
        assertEquals(360 + 19 * 280 + GenericPointBlock.SLAB, size[2], 1e-6);
        assertThrows(IllegalArgumentException.class, () -> new PointBlockSpec<>(20, 800));
        assertThrows(IllegalArgumentException.class, () -> new PointBlockSpec<>(20, 1_300));
        // a door to each flat from the lobby, in the gap its wall leaves, on the lobby's edge
        final List<FloorPlan.Wall> doors = plan.covers().stream().filter(d -> d.kind() == FloorPlan.Kind.DOOR)
                .map(FloorPlan.Cover::wall).toList();
        assertEquals(4, doors.size());
        final double edge = point.core() / 2 - GenericPointBlock.CORE_WALL / 2;
        assertTrue(doors.stream().allMatch(d -> Math.abs(d.length() - (ExecutiveFlat.DOOR_TO - ExecutiveFlat.DOOR_FROM))
                < 1e-9 && (Math.abs(Math.abs(d.x0()) - edge) < 1e-9 && d.x0() == d.x1()
                || Math.abs(Math.abs(d.z0()) - edge) < 1e-9 && d.z0() == d.z1())), "in the lobby's walls");
        assertEquals(4 * 19, block.solids(new Elements.Door()).size(), "on every storey over the void deck");
    }

    @Test
    void aWallAtAnAngleStandsAlongItsLineSoThick() {
        final Structure one = new Raised(20).of(new FloorPlan(List.of(new FloorPlan.Wall(0, 0, 300, 400, 20, "slant")),
                List.of(new FloorPlan.Slab(-100, -100, 400, 500))), 1, 300, 300);
        final Solid wall = one.solids(new Elements.BearingWall()).getFirst();
        final Point a = wall.corners().get(0);
        final Point b = wall.corners().get(2);
        assertEquals(20, Math.hypot(b.across() - a.across(), b.along() - a.along()), 1e-9, "so thick across it");
        final Point end = wall.corners().get(1);
        assertEquals(500, Math.hypot(end.across() - a.across(), end.along() - a.along()), 1e-9, "so long along it");
        assertTrue(wall.corners().get(4).up() > a.up());
    }

    @Test
    void coveredAboveTheVoidDeckInBrickTheirWindowsAsWideAsTheFlats() {
        final Structure block = new GenericSlabBlock().build(slab);
        final List<Solid> cover = new ArrayList<>(block.solids(new Elements.Wall()));
        cover.addAll(block.solids(new Elements.Window()));
        cover.addAll(block.solids(new Elements.Parapet()));
        assertTrue(cover.stream().allMatch(s -> s.corners().stream().allMatch(p -> p.up() >= slab.ground()
                - 1e-6)), "nothing on the void deck");
        assertTrue(block.solids(new Elements.Wall()).stream().allMatch(s -> s.material() == Material.BRICK));
        final List<Solid> parapets = block.solids(new Elements.Parapet());
        assertEquals(11, parapets.size(), "the access balcony guarded on every storey over the void deck");
        assertTrue(parapets.stream().allMatch(p -> Math.abs(height(p) - 100) < 1e-6), "half a storey high");
        // each home windowed between its pillars, every storey alike, eleven of them
        final long panes = block.solids(new Elements.Window()).stream().filter(s -> s.material() == Material.GLASS)
                .count();
        assertTrue(panes > 0 && panes % 11 == 0);
        final Structure tower = new GenericPointBlock().build(point);
        final long round = tower.solids(new Elements.Window()).stream().filter(s -> s.material() == Material.GLASS)
                .count();
        assertTrue(round > 0 && round % (4 * 19) == 0, "the four flats alike, on nineteen storeys");
        assertEquals(List.of(), block.problems());
        assertEquals(List.of(), tower.problems());
    }

    /** Whether {@code a} and {@code b} share no ground, but for an edge. */
    private boolean apart(final FloorPlan.Slab a, final FloorPlan.Slab b) {
        return Math.min(a.x1(), b.x1()) - Math.max(a.x0(), b.x0()) < 1e-6
                || Math.min(a.z1(), b.z1()) - Math.max(a.z0(), b.z0()) < 1e-6;
    }

    /** How high {@code solid} is, its top over its bottom. */
    private double height(final Solid solid) {
        final double low = solid.corners().stream().mapToDouble(Point::up).min().orElseThrow();
        return solid.corners().stream().mapToDouble(Point::up).max().orElseThrow() - low;
    }

    /** How far {@code solids} reach across, along and up, all told. */
    private double[] size(final List<Solid> solids) {
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (final Solid solid : solids) {
            for (final Point p : solid.corners()) {
                final double[] at = {p.across(), p.along(), p.up()};
                for (int k = 0; k < 3; k++) {
                    low[k] = Math.min(low[k], at[k]);
                    high[k] = Math.max(high[k], at[k]);
                }
            }
        }
        return new double[] {high[0] - low[0], high[1] - low[1], high[2] - low[2]};
    }
}

package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The slab block and the point block, their structure and their cover: nothing wrong with either; each storey its
 * plan's walls, its slabs over them, the topmost its roof; as high as its storeys, the first 3.6 m and the rest 2.8 m;
 * the slab block as long as its flats and cores, a core at each end and between each group; the point block four
 * executive flats on their pillars, round their lobby, clear of one another; covered above the void deck in brick,
 * the slab block's windows as wide as each flat's back, the access balcony guarded half a storey high; and blocks too
 * big or too small refused.
 */
class BlocksTest {

    private final SlabBlockSpec<Civilization, CommonEras.Modern> slab = new SlabBlockSpec<>(12, 1_020, 1_025, 180,
            6, 12);
    private final PointBlockSpec<Civilization, CommonEras.Modern> point = new PointBlockSpec<>(20, 900);

    @Test
    void theSlabBlockItsBaysItsCoresItsStoreys() {
        final Structure block = new GenericSlabBlock().build(slab);
        assertEquals(List.of(), block.problems());
        final FloorPlan plan = new GenericSlabBlock().plan(slab);
        // bays: a core, six flats, a core, six flats, a core. Each flat nine pillars of its own; on each group's seven
        // lines, flats mirrored in turn, four with two pillars and three with three; each core three walls
        assertEquals(12 * 9 + 2 * (4 * 2 + 3 * 3) + 3 * 3, plan.walls().size());
        final List<FloorPlan.Wall> pillars = plan.walls().stream().filter(w -> w.name().equals("pillar")).toList();
        assertTrue(pillars.stream().allMatch(w -> w.thick() == CorridorFlat.PILLAR && w.length() <= 180),
                "blades 30 cm thick, none longer than 1.8 m");
        assertEquals(12 * plan.walls().size(), block.solids(new Elements.BearingWall()).size());
        assertEquals(12 - 1, block.solids(new Elements.Floor()).size() - 1, "a slab over each storey but the top");
        assertEquals(1, block.solids(new Elements.Roof()).size());
        final double[] size = size(block.solids());
        assertEquals(12 * 1_020 + 3 * GenericSlabBlock.CORE_WIDE, size[0], 1e-6, "its cores' end walls flush");
        assertEquals(1_025 + 180, size[1], 1e-6, "its flats and its corridor");
        assertEquals(360 + 11 * 280 + GenericSlabBlock.SLAB, size[2], 1e-6, "from under the ground slab to the roof");
        final SlabBlockSpec<Civilization, CommonEras.Modern> wider = new SlabBlockSpec<>(10, 1_150, 1_200, 180, 5, 6);
        // wider and deeper than the board's: the same pillars, as big; five flats a group, the last unmirrored
        final List<FloorPlan.Wall> stretched = new GenericSlabBlock().plan(wider).walls();
        assertEquals(10 * 9 + 2 * (3 * 2 + 3 * 3) + 3 * 3, stretched.size());
        assertTrue(stretched.stream().filter(w -> w.name().equals("pillar"))
                .allMatch(w -> Math.abs(w.thick() - 30) < 1e-9), "pillars stretched apart, none bigger");
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
        // each flat's pillars, and the lobby's wall its side, in two, either side of its door; the shaft's four
        assertEquals(4 * (pillars.size() + 2) + 4, plan.walls().size());
        assertEquals(20 * plan.walls().size(), block.solids(new Elements.BearingWall()).size());
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
        assertThrows(IllegalArgumentException.class, () -> new SlabBlockSpec<>(12, 750, 1_200, 180, 6, 31));
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
        assertTrue(cover.stream().allMatch(s -> s.corners().stream().allMatch(p -> p.up() >= GenericSlabBlock.GROUND
                - 1e-6)), "nothing on the void deck");
        assertTrue(block.solids(new Elements.Wall()).stream().allMatch(s -> s.material() == Material.BRICK));
        final List<Solid> parapets = block.solids(new Elements.Parapet());
        assertEquals(11, parapets.size(), "the access balcony guarded on every storey over the void deck");
        assertTrue(parapets.stream().allMatch(p -> Math.abs(height(p) - 100) < 1e-6), "half a storey high");
        // each flat windowed between its pillars, back and front, every flat alike, on eleven storeys
        final long panes = block.solids(new Elements.Window()).stream().filter(s -> s.material() == Material.GLASS)
                .count();
        assertTrue(panes > 0 && panes % (11 * 12) == 0);
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

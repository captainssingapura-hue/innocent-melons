package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The slab block and the point block as bare structure: nothing wrong with either; each storey its plan's walls, its
 * slabs over them, the topmost its roof; as high as its storeys, the first 3.6 m and the rest 2.8 m; the slab block as
 * long as its flats and cores, a core at each end and between each group; the point block square, its core closed and
 * four party walls out from it; and blocks too big refused.
 */
class BlocksTest {

    private final SlabBlockSpec<Civilization, CommonEras.Modern> slab = new SlabBlockSpec<>(12, 750, 1_200, 180, 6,
            12);
    private final PointBlockSpec<Civilization, CommonEras.Modern> point = new PointBlockSpec<>(20, 1_900, 800);

    @Test
    void theSlabBlockItsBaysItsCoresItsStoreys() {
        final Structure block = new GenericSlabBlock().build(slab);
        assertEquals(List.of(), block.problems());
        final FloorPlan plan = new GenericSlabBlock().plan(slab);
        // bays: a core, six flats, a core, six flats, a core: fifteen bays, sixteen walls between and at the ends,
        // three cores' back walls and the spine
        assertEquals(16 + 3 + 1, plan.walls().size());
        assertEquals(12 * plan.walls().size(), block.solids(new Elements.BearingWall()).size());
        assertEquals(12 - 1, block.solids(new Elements.Floor()).size() - 1, "a slab over each storey but the top");
        assertEquals(1, block.solids(new Elements.Roof()).size());
        final double[] size = size(block.solids());
        assertEquals(12 * 750 + 3 * GenericSlabBlock.CORE_WIDE + GenericSlabBlock.WALL, size[0], 1e-6);
        assertEquals(1_200 + 180, size[1], 1e-6, "its flats and its corridor");
        assertEquals(360 + 11 * 280 + GenericSlabBlock.SLAB, size[2], 1e-6, "from under the ground slab to the roof");
    }

    @Test
    void thePointBlockSquareItsCoreClosedFourPartyWalls() {
        final Structure block = new GenericPointBlock().build(point);
        assertEquals(List.of(), block.problems());
        assertEquals(8, new GenericPointBlock().plan(point).walls().size());
        assertEquals(20 * 8, block.solids(new Elements.BearingWall()).size());
        final double[] size = size(block.solids());
        assertEquals(1_900, size[0], 1e-6);
        assertEquals(1_900, size[1], 1e-6);
        assertEquals(360 + 19 * 280 + GenericPointBlock.SLAB, size[2], 1e-6);
        assertThrows(IllegalArgumentException.class, () -> new PointBlockSpec<>(20, 1_900, 1_000));
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

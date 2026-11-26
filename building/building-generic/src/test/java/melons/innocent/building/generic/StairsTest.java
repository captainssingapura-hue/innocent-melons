package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.building.spec.Walling;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.forms.Tint;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The housing board's blocks' stairs: each storey climbed a riser at a time, none higher than the board allows; up
 * from every storey but the top, a rail there instead; their wells open through every slab between, the ground's and
 * the roof whole; in the slab block one to each core, in the point block one in its lobby, clear of its doors; and
 * room for them in every block their specifications allow.
 */
class StairsTest {

    private final Finish brick = new Finish(Walling.BRICK, Optional.empty(), new Tint("#3c3f44"),
            new Tint("#6f8fa3"));

    @Test
    void eachStoreyClimbedARiserAtATimeNoneHigherThanTheBoardAllows() {
        assertEquals(16, new Flights(280).risers());
        assertEquals(17.5, new Flights(280).riser(), 1e-9);
        assertEquals(7 * Flights.GOING, new Flights(280).run(), 1e-9);
        assertEquals(22, new Flights(360).risers(), "the void deck's, higher");
        for (final double rise : new double[] {260, 280, 300, 360, 500}) {
            final Flights flights = new Flights(rise);
            final List<Double> tops = new DogLeg(0, 0, 0, -1, 250, 600).storey(0, rise).stream()
                    .filter(p -> !p.solid().name().equals("stair's middle wall"))
                    .map(p -> top(p.solid())).sorted().toList();
            assertEquals(flights.risers() - 1, tops.size(), "a tread or the landing on every riser but the last");
            double was = 0;
            for (final double top : tops) {
                assertEquals(flights.riser(), top - was, 1e-9, "a riser up from the last");
                was = top;
            }
            assertEquals(flights.riser(), rise - was, 1e-9, "and one up to the floor over");
            assertTrue(flights.riser() <= Flights.RISER + 1e-9);
        }
    }

    @Test
    void theSlabBlockAStairToEachCoreItsWellsOpenBetweenGroundAndRoof() {
        final SlabBlockSpec<Civilization, CommonEras.Modern> s = new SlabBlockSpec<>(13_440, 1_205, 180, 12, 360,
                280, brick);
        final Structure block = new GenericSlabBlock().build(s);
        final FloorPlan plan = new GenericSlabBlock().plan(s);
        assertEquals(2, plan.stairs().size(), "one to each core");
        assertEquals(2 * 11, block.stairs().size(), "up from every storey but the top");
        assertEquals(2 * 2, block.solids(new Elements.Railing()).size(), "a rail along and across each at the top");
        for (final DogLeg stair : plan.stairs()) {
            open(block, stair);
        }
        for (final Structure.Stair stair : block.stairs()) {
            final double rise = stair.head().level() - stair.foot().level();
            assertTrue(rise == 360 || rise == 280, "a storey up: " + rise);
        }
    }

    @Test
    void thePointBlockAStairInItsLobbyItsWellOpenBetweenGroundAndRoof() {
        final PointBlockSpec<Civilization, CommonEras.Modern> s = new PointBlockSpec<>(20, 900);
        final Structure block = new GenericPointBlock().build(s);
        final FloorPlan plan = new GenericPointBlock().plan(s);
        assertEquals(1, plan.stairs().size());
        assertEquals(19, block.stairs().size());
        open(block, plan.stairs().getFirst());
        assertEquals(List.of(), block.problems());
    }

    @Test
    void roomForThemInEveryBlockTheirSpecificationsAllow() {
        for (final double ground : new double[] {300, 360, 500}) {
            for (final double storey : new double[] {260, 280, 360}) {
                final SlabBlockSpec<Civilization, CommonEras.Modern> s = new SlabBlockSpec<>(13_440, 1_205, 180, 3,
                        ground, storey, brick);
                assertFalse(new GenericSlabBlock().build(s).stairs().isEmpty());
                for (final DogLeg stair : new GenericSlabBlock().plan(s).stairs()) {
                    assertTrue(stair.well().z1() < 0, "its front edge on the core's floor, before the corridor");
                }
            }
        }
        for (double core = 830; core <= 1_200; core += 10) {
            final PointBlockSpec<Civilization, CommonEras.Modern> s = new PointBlockSpec<>(3, core);
            new GenericPointBlock().build(s);
            final FloorPlan plan = new GenericPointBlock().plan(s);
            final Box well = plan.stairs().getFirst().well();
            for (final FloorPlan.Cover door : plan.covers()) {
                if (door.kind() == FloorPlan.Kind.DOOR) {
                    assertTrue(clear(well, door.wall(), 100), core + ": a metre clear before each door");
                }
            }
        }
    }

    /** That {@code stair}'s well is open through every floor slab of {@code block}, and closed by its others. */
    private void open(final Structure block, final DogLeg stair) {
        final Box well = stair.well();
        final double x = (well.x0() + well.x1()) / 2;
        final double z = (well.z0() + well.z1()) / 2;
        final List<Solid> floors = block.solids(new Elements.Floor());
        assertTrue(floors.stream().anyMatch(f -> top(f) <= 0 && over(f, x, z)), "the ground slab whole");
        assertTrue(floors.stream().noneMatch(f -> top(f) > 0 && over(f, x, z)), "open through every floor");
        assertTrue(block.solids(new Elements.Roof()).stream().anyMatch(f -> over(f, x, z)), "the roof whole");
    }

    /**
     * Whether {@code box} keeps out of what lies {@code clear} before and behind {@code door}, a door square to the
     * plan, as wide as it is: the way through it.
     */
    private boolean clear(final Box box, final FloorPlan.Wall door, final double clear) {
        final double reach = door.thick() / 2 + clear;
        final Box way = door.z0() == door.z1()
                ? new Box(Math.min(door.x0(), door.x1()), door.z0() - reach, Math.max(door.x0(), door.x1()),
                door.z0() + reach)
                : new Box(door.x0() - reach, Math.min(door.z0(), door.z1()), door.x0() + reach,
                Math.max(door.z0(), door.z1()));
        return box.x1() <= way.x0() || box.x0() >= way.x1() || box.z1() <= way.z0() || box.z0() >= way.z1();
    }

    private boolean over(final Solid s, final double x, final double z) {
        return s.corners().stream().mapToDouble(Point::across).min().orElseThrow() < x
                && s.corners().stream().mapToDouble(Point::across).max().orElseThrow() > x
                && s.corners().stream().mapToDouble(Point::along).min().orElseThrow() < z
                && s.corners().stream().mapToDouble(Point::along).max().orElseThrow() > z;
    }

    private double top(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}

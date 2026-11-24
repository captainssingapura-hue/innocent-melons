package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.building.spec.SlabBlockSpec;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The slab block finished and entered: filled in between its pillars with what it is asked for, painted or not, its
 * frames and glass their colours; a door from the access balcony to each home, a corner home's at the balcony's end,
 * facing along it.
 */
class SlabFinishTest {

    private final Tint frames = new Tint("#3c3f44");
    private final Tint glass = new Tint("#6f8fa3");
    private final SlabBlockSpec<Civilization, CommonEras.Modern> slab = new SlabBlockSpec<>(13_440, 1_205, 180, 12,
            360, 280, new Finish(Walling.BRICK, Optional.empty(), frames, glass));

    @Test
    void aSlabBlockFilledInWithWhatItIsAskedFor() {
        for (final Walling walling : Walling.values()) {
            final Structure block = new GenericSlabBlock().build(new SlabBlockSpec<>(6_000, 1_205, 180, 3, 360, 280,
                    new Finish(walling, Optional.empty(), frames, glass)));
            final List<Solid> walls = block.solids(new Elements.Wall());
            assertTrue(!walls.isEmpty() && walls.stream().allMatch(s -> s.material().name().equals(walling.name())),
                    walling + " between the pillars");
            assertTrue(block.solids(new Elements.BearingWall()).stream()
                    .allMatch(s -> s.material() == Material.CONCRETE), "the pillars concrete whatever between");
            assertTrue(walls.stream().allMatch(s -> s.tint().isEmpty()), "unpainted, as it is");
        }
    }

    @Test
    void aSlabBlockPaintedItsWindowsFramedAndGlazedInTheirColours() {
        final Tint cream = new Tint("#e8dcc0");
        final Tint green = new Tint("#4f7a6a");
        final Structure block = new GenericSlabBlock().build(new SlabBlockSpec<>(6_000, 1_205, 180, 3, 360, 280,
                new Finish(Walling.PRECAST, Optional.of(cream), frames, green)));
        final List<Solid> windows = block.solids(new Elements.Window());
        assertTrue(windows.stream().filter(s -> s.material() == Material.GLASS).allMatch(s -> s.tint()
                .equals(Optional.of(green))), "its glass tinted");
        assertTrue(windows.stream().filter(s -> s.material() == Material.METAL).allMatch(s -> s.tint()
                .equals(Optional.of(frames))), "its frames their colour");
        final List<Solid> painted = new ArrayList<>(block.solids(new Elements.Wall()));
        painted.addAll(block.solids(new Elements.Parapet()));
        painted.addAll(block.solids(new Elements.BearingWall()));
        painted.addAll(block.solids(new Elements.Floor()));
        assertTrue(painted.stream().allMatch(s -> s.tint().equals(Optional.of(cream))), "painted all over");
        assertTrue(block.solids(new Elements.Roof()).stream().allMatch(s -> s.tint().isEmpty()), "but its roof");
        assertTrue(block.solids(new Elements.Wall()).stream().allMatch(s -> s.material() == Material.PRECAST),
                "precast under its paint");
    }

    @Test
    void eachHomeItsDoorOnTheBalconyTheCornersFacingAlongIt() {
        final FloorPlan plan = new GenericSlabBlock().plan(slab);
        final List<FloorPlan.Cover> doors = plan.covers().stream().filter(c -> c.kind() == FloorPlan.Kind.DOOR)
                .toList();
        assertEquals(12, doors.size(), "a door to each home");
        final List<FloorPlan.Cover> corners = doors.stream().filter(d -> d.wall().name().equals("corner's door"))
                .toList();
        assertEquals(2, corners.size());
        final FloorPlan.Cover parapet = plan.covers().stream().filter(c -> c.kind() == FloorPlan.Kind.PARAPET)
                .findFirst().orElseThrow();
        for (final FloorPlan.Cover door : doors) {
            final FloorPlan.Wall w = door.wall();
            assertEquals(SlabCover.DOOR, w.length(), 1e-9, "a metre wide");
            if (corners.contains(door)) {
                // across the balcony, at the parapet's end, facing along it
                assertEquals(w.x0(), w.x1(), 1e-9);
                assertTrue(Math.abs(w.x0() - parapet.wall().x0()) < SlabCover.COVER
                        || Math.abs(w.x0() - parapet.wall().x1()) < SlabCover.COVER, "where the balcony ends");
                assertTrue(w.z0() > 0 && w.z1() < slab.corridor(), "within the balcony's width");
            } else {
                assertEquals(w.z0(), w.z1(), 1e-9);
                assertTrue(w.z0() < 0 && w.z0() > -SlabCover.COVER, "in the home's face on the balcony");
            }
        }
        // the balcony stops short of the ends: those are the corner homes'
        assertTrue(Math.min(parapet.wall().x0(), parapet.wall().x1()) > -slab.length() / 2 + GenericSlabBlock.NARROWEST
                && Math.max(parapet.wall().x0(), parapet.wall().x1()) < slab.length() / 2 - GenericSlabBlock.NARROWEST);
        final Structure block = new GenericSlabBlock().build(slab);
        final List<Solid> leaves = block.solids(new Elements.Door());
        assertEquals(11 * 12, leaves.size(), "on every storey over the void deck");
        assertTrue(leaves.stream().allMatch(s -> s.material() == Material.TIMBER && s.tint().isEmpty()),
                "timber, unpainted");
        assertEquals(List.of(), block.problems());
    }
}

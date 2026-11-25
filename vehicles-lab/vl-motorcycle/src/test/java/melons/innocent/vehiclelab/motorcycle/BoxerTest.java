package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.frame.Frame;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The boxer: its frame of tubes sound, one piece, as heavy as such a frame is; standing on its tyres, nothing of it
 * under the ground; its engine round and alike either side, its cylinders standing out past its frame; its forks
 * turning about a line that meets the ground just ahead of where the front tyre does, as a motorcycle's steering
 * does; every one of its finishes on some part; and its livery, the boxer's own to begin with, its engine titanium,
 * each finish changed one at a time, a finish for every slot and its colour as the web says one asked for.
 */
class BoxerTest {

    private final Boxer boxer = new Boxer();
    private final List<Part> parts = boxer.parts();

    @Test
    void itsFrameSoundAndAsHeavyAsSuchAFrameIs() {
        final Frame frame = boxer.frame();
        assertEquals(List.of(), frame.problems());
        assertTrue(frame.mass() > 8 && frame.mass() < 25, "a double cradle of steel tube: " + frame.mass());
        assertEquals(24, frame.nodes().size());
    }

    @Test
    void standingOnItsTyresNothingUnderTheGround() {
        final double lowest = parts.stream().flatMap(p -> p.form().points().stream()).mapToDouble(Vec3::y).min()
                .orElseThrow();
        assertEquals(0, lowest, 1e-6, "its tyres on the ground");
        for (final Part tyre : parts.stream().filter(p -> p.name().endsWith("tyre")).toList()) {
            assertEquals(0, tyre.form().points().stream().mapToDouble(Vec3::y).min().orElseThrow(), 1e-6);
        }
    }

    @Test
    void itsEngineRoundAndAlikeEitherSide() {
        final Part left = part("left cylinder");
        final Part right = part("right cylinder");
        final double out = right.form().points().stream().mapToDouble(Vec3::x).max().orElseThrow();
        assertEquals(out, -left.form().points().stream().mapToDouble(Vec3::x).min().orElseThrow(), 1e-6);
        final double frame = boxer.frame().nodes().stream().mapToDouble(n -> Math.abs(n.across())).max()
                .orElseThrow();
        assertTrue(out > frame + 20, "its cylinders out past its frame: " + out);
        assertTrue(parts.stream().filter(p -> p.slot() == Part.Slot.ENGINE).allMatch(p -> p.moves()
                == Part.Moves.BODY));
    }

    @Test
    void itsForksTurnAboutALineMeetingTheGroundAheadOfItsTyre() {
        final Vec3 down = boxer.steering();
        final Vec3 ground = Boxer.HEAD_TOP.plus(down.times(Boxer.HEAD_TOP.y() / -down.y()));
        final double trail = ground.z() - Boxer.FRONT_AXLE.z();
        assertTrue(trail > 8 && trail < 18, "the trail a motorcycle's steering has: " + trail);
        final double rake = Math.toDegrees(Math.atan2(down.z(), -down.y()));
        assertTrue(rake > 28 && rake < 36, "raked back as a cruiser's: " + rake);
        assertTrue(parts.stream().filter(p -> p.name().startsWith("front")).allMatch(p -> p.moves()
                != Part.Moves.BODY), "what is at the front turns with the bars");
    }

    @Test
    void everyFinishOnSomePart() {
        for (final Part.Slot slot : Part.Slot.values()) {
            assertTrue(parts.stream().anyMatch(p -> p.slot() == slot), slot + " on some part");
        }
    }

    @Test
    void itsLiveryTheBoxersOwnEachFinishChangedOneAtATime() {
        final Livery own = new Livery();
        assertEquals(Coat.Kind.TITANIUM, own.coat(Part.Slot.ENGINE).kind());
        final Livery gold = own.with(Part.Slot.ENGINE, new Coat(Coat.Kind.GOLD));
        assertEquals(Coat.Kind.GOLD, gold.coat(Part.Slot.ENGINE).kind());
        assertEquals(own.coat(Part.Slot.TANK), gold.coat(Part.Slot.TANK));
        assertTrue(Arrays.equals(new double[] {1, 0, 0}, new Coat(Coat.Kind.GLOSS_PAINT, "#ff0000").rgb()));
        assertThrows(IllegalArgumentException.class, () -> new Livery(Map.of(Part.Slot.FRAME,
                new Coat(Coat.Kind.STEEL))));
        assertThrows(IllegalArgumentException.class, () -> new Coat(Coat.Kind.GLOSS_PAINT, "red"));
    }

    private Part part(final String name) {
        return parts.stream().filter(p -> p.name().equals(name)).findFirst().orElseThrow();
    }
}

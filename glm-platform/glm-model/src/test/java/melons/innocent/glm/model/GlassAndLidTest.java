package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's boot lid and glass: the lid hinged to the shell between the body sides' tops along the boot; the
 * windscreen, the rear window and the glass roof bonded to the roof's headers, the roof in a group of its own; each
 * door's window and pane carried by the door, and a quarter light each side; all of it glass, within the body.
 */
class GlassAndLidTest {

    private final SantanaLines lines = new SantanaLines();
    private final Platform platform = new Santana().platform(null).platform();
    private final Assembled built = new Assembly(platform).assembled();

    @Test
    void everyPaneGlassWhereItBelongs() {
        assertEquals(List.of(), built.problems(1e-6));
        final Set<String> glass = built.placed().keySet().stream()
                .filter(p -> p.part().made().material() == Material.GLASS).map(PartAt::said)
                .collect(Collectors.toSet());
        final Set<String> expected = new HashSet<>(Set.of("windscreen", "rear window", "glass roof"));
        for (final String pane : List.of("front door vent", "front door window", "rear door window", "rear door pane",
                "quarter light")) {
            expected.add("right " + pane);
            expected.add("left " + pane);
        }
        assertEquals(expected, glass);
        assertEquals(Group.ROOF, part("glass roof", Side.MIDDLE).part().group());
        assertEquals(Group.GLASS, part("windscreen", Side.MIDDLE).part().group());
        for (final String pane : List.of("front door vent", "front door window")) {
            assertEquals("front door", carrier(pane).name());
        }
        for (final String pane : List.of("rear door window", "rear door pane")) {
            assertEquals("rear door", carrier(pane).name());
        }
        assertEquals("roof header, front", carrier("windscreen").name());
        assertEquals("roof header, rear", carrier("rear window").name());
    }

    @Test
    void allOfItWithinTheBody() {
        for (final PartAt p : built.placed().keySet()) {
            if (p.part().made().material() == Material.GLASS || p.part().name().equals("boot lid")) {
                final Vec3 at = built.at(p);
                for (final Vec3 v : p.shape().form().points()) {
                    final Vec3 q = v.plus(at);
                    assertTrue(Math.abs(q.x()) <= lines.half(q.z(), Math.min(q.y(), 140)) + 1e-6,
                            p.said() + " outside the body at " + q);
                }
            }
        }
    }

    @Test
    void theBootLidHingedBetweenTheSidesTops() {
        final PartAt lid = part("boot lid", Side.MIDDLE);
        assertEquals(Group.BODYWORK, lid.part().group());
        assertTrue(platform.connections().stream().anyMatch(c -> c.carried().name().equals("boot lid")
                && c.joining() == Joining.HINGE && c.carrier().name().equals("underbody")));
        final Vec3 at = built.at(lid);
        final Loop side = new Loop(lines.side());
        for (final Vec3 v : lid.shape().form().points()) {
            final Vec3 q = v.plus(at);
            assertTrue(q.y() <= side.top(q.z()) + 3.1, "no higher than the sides' tops and its crown: " + q);
            assertTrue(q.y() >= side.top(q.z()) - 3.1, "no lower than its edges are turned down: " + q);
        }
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }

    private Part carrier(final String carried) {
        return platform.connections().stream().filter(c -> c.carried().name().equals(carried))
                .map(Connection::carrier).findFirst().orElseThrow();
    }
}

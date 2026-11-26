package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.vehiclelab.forms.Spot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's four doors: each hung on its hinge in the body side, pressed steel, painted; every bit of it
 * inside its opening, clear of the body side by the shut gap all round; its window open above the waist either side
 * of its divider.
 */
class DoorTest {

    private final SantanaLines lines = new SantanaLines();
    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();

    @Test
    void fourDoorsEachHungOnItsHinge() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final String name : List.of("front door", "rear door")) {
            for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
                final PartAt door = door(name, side);
                assertEquals(Material.STEEL, door.part().made().material());
                assertFalse(door.part().made().bare(), "painted");
                assertEquals(Group.BODYWORK, door.part().group());
            }
        }
        assertEquals(2, new Santana().platform(null).platform().connections().stream()
                .filter(c -> c.joining() == Joining.HINGE && c.carried().name().endsWith("door")).count());
    }

    @Test
    void eachInsideItsOpeningByTheShutGap() {
        check("front door", lines.frontDoor());
        check("rear door", lines.rearDoor());
    }

    @Test
    void itsWindowOpenEitherSideOfItsDivider() {
        final double up = (lines.belt() + lines.frontHeader().v()) / 2 - 8;
        open("front door", (lines.frontDivider() + lines.rearDoor().getFirst().u()) / 2, up);
        open("front door", lines.frontDivider() + 6, lines.belt() + 6);
        open("rear door", (lines.rearDivider() + lines.rearDoor().getFirst().u()) / 2, up);
        open("rear door", lines.rearDivider() - 5, lines.belt() + 4);
    }

    /**
     * Nothing of the right {@code name} proud of the body side; and its skin, flush with it, seen from the side, inside
     * {@code opening} by the shut gap, its edges turned in alongside the opening's own flanges.
     */
    private void check(final String name, final List<Spot> opening) {
        final PartAt door = door(name, Side.RIGHT);
        final Vec3 at = built.at(door);
        int skin = 0;
        for (final Vec3 p : door.shape().form().points()) {
            final Vec3 q = p.plus(at);
            final Spot s = new Spot(q.z(), q.y());
            assertTrue(q.x() <= lines.half(q.z(), q.y()) + 0.1, name + " proud of the body side at " + q);
            if (Math.abs(q.x() - lines.half(q.z(), q.y())) < 1e-6) {
                assertTrue(inside(s, opening), name + " outside its opening at " + s);
                assertTrue(apart(s, opening) > 0.4 - 1e-3, name + " too near its opening's edge at " + s);
                skin++;
            }
        }
        assertTrue(skin > 100, name + "'s skin flush with the body side");
    }

    /** No point of the right {@code name} near {@code u} along and {@code v} up: the glass's opening. */
    private void open(final String name, final double u, final double v) {
        final PartAt door = door(name, Side.RIGHT);
        final Vec3 at = built.at(door);
        assertTrue(door.shape().form().points().stream().map(p -> p.plus(at))
                .noneMatch(q -> Math.abs(q.z() - u) < 1.5 && Math.abs(q.y() - v) < 1.5), name + " open at " + u
                + ", " + v);
    }

    private boolean inside(final Spot s, final List<Spot> loop) {
        boolean in = false;
        for (int k = 0, j = loop.size() - 1; k < loop.size(); j = k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get(j);
            if (a.v() > s.v() != b.v() > s.v() && s.u() < (b.u() - a.u()) * (s.v() - a.v()) / (b.v() - a.v()) + a.u()) {
                in = !in;
            }
        }
        return in;
    }

    private double apart(final Spot s, final List<Spot> loop) {
        double least = Double.MAX_VALUE;
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            final double du = b.u() - a.u();
            final double dv = b.v() - a.v();
            final double t = Math.clamp(((s.u() - a.u()) * du + (s.v() - a.v()) * dv) / (du * du + dv * dv), 0, 1);
            least = Math.min(least, Math.hypot(s.u() - a.u() - t * du, s.v() - a.v() - t * dv));
        }
        return least;
    }

    private PartAt door(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}

package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engines fired: each of the range in its makers' order, evenly, every cylinder at the top of its bore as it
 * fires; the crank turning clockwise seen from its front, carrying its pins where its figures say; and each cylinder's
 * chamber, at its top, no more than the head's.
 */
class FiringTest {

    private final EngineRange range = new EngineRange();

    @Test
    void eachOfTheRangeInItsMakersOrderEvenlyEveryCylinderAtItsTop() {
        final List<String> orders = range.all().stream().map(e -> new Firing(e).written()).toList();
        assertEquals(List.of("1-3-4-2", "1-2-4-5-3", "1-4-3-6-2-5", "1-3-7-2-6-5-4-8",
                "1-7-5-11-3-9-6-12-2-8-4-10"), orders);
        for (final EngineFigures e : range.all()) {
            final Firing firing = new Firing(e);
            final List<Integer> order = firing.order();
            for (int i = 0; i < order.size(); i++) {
                final int n = order.get(i);
                final double at = firing.fires(firing.bank(n), firing.k(n));
                final int next = order.get((i + 1) % order.size());
                final double gap = (firing.fires(firing.bank(next), firing.k(next)) - at + Firing.CYCLE)
                        % Firing.CYCLE;
                assertEquals(Firing.CYCLE / order.size(), gap, 1e-9, e.name() + " evenly after " + n);
                final Cylinder c = new Cylinder(e, firing.bank(n), firing.k(n));
                assertEquals(e.stroke() / 2 + e.rod(), c.smallEnd(at).dot(c.up()), 1e-9,
                        e.name() + " " + n + " at its top as it fires");
                assertEquals(0, firing.since(firing.bank(n), firing.k(n), at), 1e-9);
            }
        }
    }

    @Test
    void theCrankTurningClockwiseSeenFromItsFrontCarryingItsPins() {
        for (final EngineFigures e : range.all()) {
            final EngineInternals inside = new EngineInternals(e);
            final Part crank = inside.platform().connections().getFirst().carried();
            final CrankFigures figures = new CrankFigures(e);
            for (final double turned : List.of(10.0, 137.0, 300.0)) {
                final Mat4 m = inside.moved(crank, turned);
                for (int b = 0; b < e.banks(); b++) {
                    for (int k = 0; k < e.perBank(); k++) {
                        assertEquals(0, moved(m, figures.pin(b, k)).minus(figures.pin(b, k, turned)).length(), 1e-9,
                                e.name() + " pin " + (b + 1) + "." + (k + 1) + " at " + turned);
                    }
                }
            }
            // seen from the front, looking back along the crank (+z), its right is +x, the camera's right being its up
            // across its ahead: the first pin, just past straight up, goes right
            assertTrue(figures.pin(0, 0, 10 - figures.angle(0, 0)).x() > 0, e.name());
        }
    }

    @Test
    void eachChamberAtItsTopNoMoreThanTheHeads() {
        final EngineFigures e = range.v6();
        final Firing firing = new Firing(e);
        final EngineInternals inside = new EngineInternals(e);
        for (int k = 0; k < e.perBank(); k++) {
            final Mat4 at = inside.chamber(1, k, firing.top(1, k));
            final Vec3 high = moved(at, new Vec3(0, 1, 0)).minus(moved(at, new Vec3(0, 0, 0)));
            assertEquals(0, high.length(), 1e-9, "its crown flush with the deck");
            final Mat4 down = inside.chamber(1, k, firing.top(1, k) + 180);
            assertEquals(e.stroke(), moved(down, new Vec3(0, 1, 0)).minus(moved(down, new Vec3(0, 0, 0))).length(),
                    1e-9);
        }
        final double roof = inside.flame().points().stream().mapToDouble(Vec3::y).max().orElseThrow();
        assertEquals(new HeadFigures(e).ridge(), roof, 1e-9, "the head's chamber up to its ridge");
    }

    private Vec3 moved(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }
}

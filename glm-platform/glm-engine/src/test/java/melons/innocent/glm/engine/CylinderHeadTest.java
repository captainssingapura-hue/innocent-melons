package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The cylinder heads: each engine's bolted to its decks, cast aluminium; every valve's head inside its bore and clear
 * of the others; each roof over its deck all round; the camshafts inside the head; and a V's two heads clear of each
 * other all the way up.
 */
class CylinderHeadTest {

    private final EngineRange range = new EngineRange();

    @Test
    void eachEnginesBoltedToItsDecks() {
        for (final EngineFigures e : range.all()) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled built = new Assembly(inside.platform()).assembled();
            assertEquals(List.of(), built.problems(1e-6), e.name());
            final long heads = built.placed().keySet().stream().filter(p -> inside.head(p.part())).count();
            assertEquals(e.banks(), heads, e.name() + " a head on each bank");
            built.placed().keySet().stream().filter(p -> inside.head(p.part())).forEach(p -> {
                assertEquals(Material.ALUMINIUM, p.part().made().material());
                assertEquals(0, built.at(p).length(), 1e-9, "cast in the block's own frame");
            });
        }
    }

    @Test
    void everyValveInsideItsBoreAndClearOfTheOthers() {
        for (final EngineFigures e : range.all()) {
            final HeadFigures head = new HeadFigures(e);
            final List<Valve> valves = List.of(new Valve(head, true, -1), new Valve(head, true, 1),
                    new Valve(head, false, -1), new Valve(head, false, 1));
            for (final Valve v : valves) {
                assertTrue(v.flat().length() + v.radius() <= head.radius() - 0.15, e.name() + " " + v);
                for (final Valve w : valves) {
                    if (v != w) {
                        assertTrue(v.flat().minus(w.flat()).length() >= v.radius() + w.radius() + 0.15,
                                e.name() + " " + v + " and " + w);
                    }
                }
            }
            assertTrue(head.roof(head.radius()) > 0, e.name() + " its roof over its deck all round");
            for (final boolean inlet : List.of(true, false)) {
                assertTrue(Math.abs(head.cam(inlet).x()) + head.journal() < head.side(inlet), e.name());
            }
        }
    }

    @Test
    void aVsTwoHeadsClearOfEachOtherAllTheWayUp() {
        for (final EngineFigures e : List.of(range.v6(), range.v8(), range.v12())) {
            final HeadFigures head = new HeadFigures(e);
            for (final double h : List.of(0.0, head.high() / 2, head.high() + head.journal() + 0.6)) {
                final Vec3 a = head.at(0, 0, new Vec3(-head.side(false), h, 0));
                final Vec3 b = head.at(1, 0, new Vec3(-head.side(false), h, 0));
                final double apart = Math.abs(a.x() - b.x());
                assertTrue(apart > 2, e.name() + " " + apart + " apart at " + h);
                assertTrue(Math.signum(a.x()) == Math.signum(e.lean(0)) || Math.abs(a.x()) < 1e-9,
                        e.name() + " each head on its own side");
            }
        }
    }

    /** A valve's seat seen from up the bore: where its middle is across the deck, and how far round its head is. */
    private record Valve(HeadFigures head, boolean inlet, int side) {

        Vec3 flat() {
            final Vec3 seat = head.seat(inlet, side);
            return new Vec3(seat.x(), 0, seat.z());
        }

        double radius() {
            return head.valve(inlet) / 2;
        }
    }
}

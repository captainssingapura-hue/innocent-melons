package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Each block's section: drawn round once, its walls never crossing, whichever way its banks lean; and its crankcase
 * clear of all that turns on the crank, every wall but the open bottom further from the crank's axis than the crank's
 * webs and rods' bolts reach.
 */
class BlockFormTest {

    private final EngineRange range = new EngineRange();

    @Test
    void drawnRoundOnceItsWallsNeverCrossing() {
        for (final EngineFigures e : range.all()) {
            final List<Spot> section = new BlockForm(e).section();
            double area = 0;
            for (int i = 0; i < section.size(); i++) {
                final Spot a = section.get(i);
                final Spot b = section.get((i + 1) % section.size());
                area += a.u() * b.v() - b.u() * a.v();
                for (int j = i + 2; j < section.size(); j++) {
                    if ((j + 1) % section.size() != i) {
                        assertFalse(crossing(a, b, section.get(j), section.get((j + 1) % section.size())),
                                e.name() + " edges " + i + " and " + j + " cross");
                    }
                }
            }
            assertTrue(area > 0, e.name() + " drawn round to the left");
        }
    }

    @Test
    void itsCrankcaseClearOfAllThatTurns() {
        for (final EngineFigures e : range.all()) {
            final List<Spot> section = new BlockForm(e).section();
            final double swing = new CrankFigures(e).swing();
            for (int i = 1; i < section.size(); i++) {
                final double d = distance(section.get(i), section.get((i + 1) % section.size()));
                assertTrue(d >= swing + 0.5, e.name() + " wall " + i + " " + d + " from the axis, the crank " + swing);
            }
        }
    }

    /** How near the wall from {@code a} to {@code b} comes to the crank's axis. */
    private double distance(final Spot a, final Spot b) {
        final double du = b.u() - a.u();
        final double dv = b.v() - a.v();
        final double t = Math.clamp(-(a.u() * du + a.v() * dv) / (du * du + dv * dv), 0, 1);
        return Math.hypot(a.u() + t * du, a.v() + t * dv);
    }

    private boolean crossing(final Spot a, final Spot b, final Spot c, final Spot d) {
        return side(a, b, c) * side(a, b, d) < 0 && side(c, d, a) * side(c, d, b) < 0;
    }

    private double side(final Spot a, final Spot b, final Spot p) {
        return Math.signum((b.u() - a.u()) * (p.v() - a.v()) - (b.v() - a.v()) * (p.u() - a.u()));
    }
}

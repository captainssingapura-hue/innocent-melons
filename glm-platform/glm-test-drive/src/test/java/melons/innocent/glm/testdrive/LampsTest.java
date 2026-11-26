package melons.innocent.glm.testdrive;

import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.model.Santana;
import melons.innocent.vehiclelab.forms.Coat;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's lamps as it is driven: none lit to begin with; the brake lamps bright while the brake is pressed
 * rolling ahead or the throttle rolling back, and not at a standstill; the reversing lamps while rolling back; F
 * putting the headlamps and the tail lamps on and off; G stepping the indicators to the left, the right, the hazard
 * lights and off, each flashing a flash and a half a second; and the lamps that are not lit left in the day's light.
 */
class LampsTest {

    private static final double STEP = 1.0 / 60;
    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Lamps lamps = new Lamps();

    @Test
    void noneLitToBeginWithTheBrakeLampsWhileBrakingRolling() {
        lamps.told(none(), 0, 0, STEP);
        built.placed().keySet().forEach(p -> assertTrue(lamps.lit(p).isEmpty(), p.said() + " lit"));
        lamps.told(none(), -1, 800, STEP);
        assertTrue(lit("tail lamp", Side.RIGHT) && lit("tail lamp", Side.LEFT), "braking rolling ahead");
        lamps.told(none(), -1, 0, STEP);
        assertFalse(lit("tail lamp", Side.RIGHT), "not at a standstill");
        lamps.told(none(), -1, -200, STEP);
        assertFalse(lit("tail lamp", Side.RIGHT), "reversing, not braking");
        assertTrue(lit("reversing lamp", Side.RIGHT) && lit("reversing lamp", Side.LEFT), "the reversing lamps");
        lamps.told(none(), 1, -200, STEP);
        assertTrue(lit("tail lamp", Side.LEFT), "the throttle rolling back brakes it");
        assertEquals(Coat.Kind.LAMP, lamps.lit(part("tail lamp", Side.LEFT)).orElseThrow().kind(), "glowing");
    }

    @Test
    void fPutsTheLightsOnAndOff() {
        lamps.told(pressed("F"), 0, 0, STEP);
        assertTrue(lit("headlamp", Side.RIGHT) && lit("tail lamp", Side.LEFT), "on");
        lamps.told(none(), -1, 800, STEP);
        assertTrue(!lamps.lit(part("tail lamp", Side.LEFT)).orElseThrow().equals(dim()), "brighter braking");
        lamps.told(pressed("F"), 0, 0, STEP);
        assertFalse(lit("headlamp", Side.RIGHT) || lit("tail lamp", Side.LEFT), "off again");
    }

    @Test
    void gStepsTheIndicatorsEachFlashing() {
        lamps.told(pressed("G"), 0, 0, STEP);
        assertTrue(lit("indicator", Side.LEFT) && lit("rear indicator", Side.LEFT), "left, front and rear");
        assertFalse(lit("indicator", Side.RIGHT), "not the right");
        for (int k = 0; k < 25; k++) {
            lamps.told(none(), 0, 0, STEP);
        }
        assertFalse(lit("indicator", Side.LEFT), "dark between flashes");
        for (int k = 0; k < 15; k++) {
            lamps.told(none(), 0, 0, STEP);
        }
        assertTrue(lit("indicator", Side.LEFT), "alight again");
        lamps.told(pressed("G"), 0, 0, STEP);
        assertTrue(lit("indicator", Side.RIGHT) && !lit("indicator", Side.LEFT), "the right");
        lamps.told(pressed("G"), 0, 0, STEP);
        assertTrue(lit("indicator", Side.RIGHT) && lit("rear indicator", Side.LEFT), "the hazard lights");
        lamps.told(pressed("G"), 0, 0, STEP);
        assertFalse(lit("indicator", Side.RIGHT) || lit("indicator", Side.LEFT), "off");
    }

    private Coat dim() {
        final Lamps on = new Lamps();
        on.told(pressed("F"), 0, 0, STEP);
        return on.lit(part("tail lamp", Side.LEFT)).orElseThrow();
    }

    private boolean lit(final String name, final Side side) {
        return lamps.lit(part(name, side)).isPresent();
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }

    private Step none() {
        return new Step(Set.of(), List.of(), 0, 0, false, 0, 0, false);
    }

    /** {@code key} pressed, F as the engine tells it: the step it takes off or lands in. */
    private Step pressed(final String key) {
        return "F".equals(key) ? new Step(Set.of(), List.of(), 0, 0, true, 0, 0, false)
                : new Step(Set.of(), List.of(key), 0, 0, false, 0, 0, false);
    }
}

package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.sound.Engine;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineRange;
import melons.innocent.glm.engine.Firing;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The GLM engines' voices: each firing as its crank fires it in its own order, evenly but the twin, each cylinder
 * into its own bank's pipe; turbocharged when asked, but the twin; and quicker to rev in titanium than in steel.
 */
class EngineVoicesTest {

    private final EngineVoices voices = new EngineVoices();

    @Test
    void eachFiringAsItsCrankFiresItInItsOwnOrderIntoItsOwnBanksPipe() {
        for (final EngineFigures e : new EngineRange().all()) {
            final Engine voice = voices.voiced(e, Material.TITANIUM, false);
            final Firing firing = new Firing(e);
            assertArrayEquals(firing.order().stream().mapToInt(Integer::intValue).toArray(), voice.exhaust().order(),
                    e.name());
            final double[] when = voice.firing();
            double at = 0;
            for (int i = 0; i < when.length; i++) {
                assertEquals(at, when[i], 1e-9, e.name() + " as its crank fires it");
                assertEquals(firing.bank(firing.order().get(i)), voice.bank()[i], e.name() + " into its bank");
                at += firing.gaps().get(i);
            }
            assertTrue(e.twin() || firing.even(), e.name() + " evenly");
            assertTrue(!voice.charged());
            assertEquals(!e.twin(), voices.voiced(e, Material.TITANIUM, true).turbocharged(),
                    e.name() + " turbocharged, but the twin");
        }
    }

    @Test
    void quickerToRevInTitaniumThanInSteel() {
        final EngineFigures v8 = new EngineRange().v8();
        final Engine titanium = voices.voiced(v8, Material.TITANIUM, false);
        final Engine steel = voices.voiced(v8, Material.STEEL, false);
        assertTrue(titanium.climb() > steel.climb() * 1.15, Arrays.toString(new double[] {titanium.climb(),
            steel.climb()}));
        assertEquals(steel.idle(), titanium.idle(), 1e-9);
    }
}

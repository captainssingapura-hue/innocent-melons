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
 * The GLM engines' voices: each firing evenly in its own order, each cylinder into its own bank's pipe; turbocharged
 * when asked; and quicker to rev in titanium than in steel.
 */
class EngineVoicesTest {

    private final EngineVoices voices = new EngineVoices();

    @Test
    void eachFiringEvenlyInItsOwnOrderIntoItsOwnBanksPipe() {
        for (final EngineFigures e : new EngineRange().all()) {
            final Engine voice = voices.voiced(e, Material.TITANIUM, false);
            final Firing firing = new Firing(e);
            assertArrayEquals(firing.order().stream().mapToInt(Integer::intValue).toArray(), voice.exhaust().order(),
                    e.name());
            final double[] when = voice.firing();
            for (int i = 0; i < when.length; i++) {
                assertEquals(i * Firing.CYCLE / when.length, when[i], 1e-9, e.name() + " evenly");
                assertEquals(firing.bank(firing.order().get(i)), voice.bank()[i], e.name() + " into its bank");
            }
            assertTrue(!voice.charged());
            assertTrue(voices.voiced(e, Material.TITANIUM, true).turbocharged(), e.name() + " turbocharged");
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

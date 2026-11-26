package melons.innocent.terrain.drawn;

import com.example.fxsuite.texture.materials.Materials;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.melon.MelonFeatures;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Melon country in the lab's materials: plain first, everywhere; each feature in the material its kind is made
 * of, where the lab has one, and plain where it has none; the legend saying which.
 */
class ByMaterialsTest {

    private final ByMaterials painting = new ByMaterials(MelonFeatures.INSTANCE.features());

    @Test
    void plainFirstEverywhere() {
        assertEquals(Optional.empty(), painting.material(0));
        assertTrue(painting.covers(0, new Sample(0, 0, -5_000, 3)));
        assertEquals("plain", painting.legend().getFirst().label());
    }

    @Test
    void eachInTheMaterialItsKindIsMadeOfPlainWhereTheLabHasNone() {
        // laid: meadows, scrub, moss, beaches, crags, the two woods, the two fields, the river, the bay
        assertEquals(Optional.of(new Materials.Turf()), painting.material(1));
        assertEquals(Optional.empty(), painting.material(2), "scrub: none yet");
        assertEquals(Optional.empty(), painting.material(3), "moss: none yet");
        assertEquals(Optional.of(new Materials.Sand()), painting.material(4));
        assertEquals(Optional.of(new Materials.Bedrock()), painting.material(5));
        assertEquals(Optional.empty(), painting.material(6), "a wood: none yet");
        assertEquals(Optional.of(new Materials.Topsoil()), painting.material(8));
        assertEquals(Optional.of(new Materials.Water()), painting.material(10));
        assertEquals(Optional.of(new Materials.Water()), painting.material(11));
        assertEquals("the Melon meadows: turf", painting.legend().get(1).label());
        assertEquals("the West Wood: plain", painting.legend().get(7).label());
    }
}

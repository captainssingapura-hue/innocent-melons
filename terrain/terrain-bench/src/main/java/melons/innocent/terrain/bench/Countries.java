package melons.innocent.terrain.bench;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.LandText;
import melons.innocent.terrain.meadow.MeadowFeatures;
import melons.innocent.terrain.melon.MelonFeatures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The country a bench draws, as its properties ask: {@code -Dbench.map=} a country's text, written anywhere, by any
 * project ({@link LandText}); or else {@code -Dbench.country=} one of those here, {@code melon}, the Melon country
 * (the first), or {@code meadow}, the meadow round the village.
 */
public record Countries() implements StatelessFunctionalObject {

    public static final Countries INSTANCE = new Countries();

    /** The country asked for. */
    public LandFeatures chosen() throws IOException {
        final String map = System.getProperty("bench.map", "");
        if (!map.isEmpty()) {
            return LandText.INSTANCE.parse(Files.readString(Path.of(map), StandardCharsets.UTF_8));
        }
        return "meadow".equals(System.getProperty("bench.country", "melon")) ? MeadowFeatures.INSTANCE.features()
                : MelonFeatures.INSTANCE.features();
    }
}

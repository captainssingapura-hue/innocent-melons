package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.bench.Walk;
import com.example.fxsuite.vulkan.flight.Flying;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.LandText;
import melons.innocent.terrain.meadow.MeadowFeatures;
import melons.innocent.terrain.melon.MelonFeatures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * A flight over a country, opened: a bot on its jets over the land, flying only, kept half a metre over the ground
 * and the water, in the walks' windows, with their sky, shadows, kept runs and films ({@link Walk}). Which country:
 * {@code -Dbench.map=} a country's text, written by any project (Marina Bay's land, written by its own bay-terrain
 * module, say); or else {@code -Dbench.country=} {@code melon} (the Melon country, the first) or {@code meadow} (the
 * meadow round the village). It starts over the country's middle, forty metres up, unless {@code -Dbench.at=} says
 * otherwise (so far across and along, in metres, which way round, in degrees, and how high over what is below, in
 * metres).
 */
public final class CountryFlight {

    public static void main(final String[] args) throws IOException {
        final LandFeatures country = new Asked().country();
        final Grounded below = new Grounded(country);
        final Extent extent = country.ground().extent();
        final String middle = String.format(Locale.ROOT, "%.1f,%.1f,0,40", (extent.x0() + extent.x1()) / 200,
                (extent.z0() + extent.z1()) / 200);
        new Walk(country.name() + ", flown over", (card, laid) -> new CountryPlace(card, laid, country, below),
                new Flying(below, middle)).run();
    }

    /** The country a flight is asked for, by its properties. */
    private record Asked() {

        /** The country asked for. */
        LandFeatures country() throws IOException {
            final String map = System.getProperty("bench.map", "");
            if (!map.isEmpty()) {
                return LandText.INSTANCE.parse(Files.readString(Path.of(map), StandardCharsets.UTF_8));
            }
            return "meadow".equals(System.getProperty("bench.country", "melon")) ? MeadowFeatures.INSTANCE.features()
                    : MelonFeatures.INSTANCE.features();
        }
    }
}

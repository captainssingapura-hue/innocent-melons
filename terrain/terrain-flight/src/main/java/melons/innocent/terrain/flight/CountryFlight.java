package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.bench.Walk;
import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.flight.Piloting;
import com.example.fxsuite.vulkan.flight.Routing;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehicles.drawn.OspreyDrawn;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.LandText;
import melons.innocent.terrain.meadow.MeadowFeatures;
import melons.innocent.terrain.meadow.MeadowMap;
import melons.innocent.terrain.meadow.MeadowTopography;
import melons.innocent.terrain.melon.MelonFeatures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * A flight over a country, opened: the Osprey, flown from the keyboard over the land, its display over the picture
 * and its panel clicked with the mouse, kept half a metre over the ground and the water, in the walks' windows, with
 * their sky, shadows, kept runs and films ({@link Walk}). Which country:
 * {@code -Dbench.map=} a country's text, written by any project (Marina Bay's land, written by its own bay-terrain
 * module, say); or else {@code -Dbench.country=} {@code melon} (the Melon country, the first) or {@code meadow} (the
 * meadow round the village). It starts on the ground at the country's middle, unless {@code -Dbench.at=} says
 * otherwise (so far across and along, in metres, and which way round, in degrees). Or, told {@code
 * -Dbench.route=river} over the meadow, it flies the river from end to end by itself, at {@code
 * -Dbench.route.speed=} kilometres an hour (250 unless said), {@code -Dbench.route.above=} metres over the highest of
 * what lies ahead (120 unless said); or {@code -Dbench.route=straight}, along the world from the middle, as far as
 * that speed goes in {@code -Dbench.route.seconds=} (10 unless said).
 */
public final class CountryFlight {

    public static void main(final String[] args) throws IOException {
        final LandFeatures country = new Asked().country();
        final Grounded below = new Grounded(country);
        final Extent extent = country.ground().extent();
        final String middle = String.format(Locale.ROOT, "%.1f,%.1f,0", (extent.x0() + extent.x1()) / 200,
                (extent.z0() + extent.z1()) / 200);
        final Asked asked = new Asked();
        final List<Vec3> route = asked.route(country);
        final Roamer roamer = route.isEmpty() ? new Piloting(below, middle, OspreyDrawn::new)
                : new Routing(route, Double.parseDouble(System.getProperty("bench.route.speed", "250")) / 0.036,
                        Double.parseDouble(System.getProperty("bench.route.above", "120")) * 100, below,
                        OspreyDrawn::new);
        new Walk(country.name() + ", flown over in an Osprey",
                (card, laid) -> new CountryPlace(card, laid, country, below), roamer).run();
    }

    /** The country a flight is asked for, by its properties. */
    private record Asked() {

        /**
         * The route the craft flies by itself, if one is asked for: {@code -Dbench.route=river}, the meadow's river
         * from its west end to its east. None, and it is flown from the keyboard.
         */
        List<Vec3> route(final LandFeatures country) {
            final String said = System.getProperty("bench.route", "");
            if ("straight".equals(said)) {
                // from the middle, along the world, as far as the speed goes in the seconds asked
                final Extent e = country.ground().extent();
                final double far = Double.parseDouble(System.getProperty("bench.route.speed", "250")) / 0.036
                        * Double.parseDouble(System.getProperty("bench.route.seconds", "10"));
                final Vec3 middle = new Vec3((e.x0() + e.x1()) / 2, 0, (e.z0() + e.z1()) / 2);
                return List.of(middle, middle.plus(new Vec3(0, 0, far)));
            }
            if (!"river".equals(said)) {
                return List.of();
            }
            if (!"meadow".equals(System.getProperty("bench.country", "melon"))) {
                throw new IllegalArgumentException("the river flown is the meadow's: -Dbench.country=meadow");
            }
            return MeadowMap.INSTANCE.river(MeadowTopography.INSTANCE.topography()).stream()
                    .map(spot -> new Vec3(spot.x(), 0, spot.z())).toList();
        }

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

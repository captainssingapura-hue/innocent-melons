package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.space.Vec3;
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

/** The country a flight or cruise is asked for, by its properties, and where on it things begin. */
record Chosen() {

    /** The country asked for: {@code -Dbench.map=} a country's text, or {@code -Dbench.country=} melon or meadow. */
    LandFeatures country() throws IOException {
        final String map = System.getProperty("bench.map", "");
        if (!map.isEmpty()) {
            return LandText.INSTANCE.parse(Files.readString(Path.of(map), StandardCharsets.UTF_8));
        }
        return meadow() ? MeadowFeatures.INSTANCE.features() : MelonFeatures.INSTANCE.features();
    }

    /** Whether the meadow is asked for, and no map. */
    boolean meadow() {
        return System.getProperty("bench.map", "").isEmpty()
                && "meadow".equals(System.getProperty("bench.country", "melon"));
    }

    /** The country's middle, as {@code -Dbench.at=} says a place: across and along in metres, round in degrees. */
    String middle(final LandFeatures country) {
        final Extent extent = country.ground().extent();
        return String.format(Locale.ROOT, "%.1f,%.1f,0", (extent.x0() + extent.x1()) / 200,
                (extent.z0() + extent.z1()) / 200);
    }

    /** The meadow's river, from its west end to its east. */
    List<Vec3> river() {
        return MeadowMap.INSTANCE.river(MeadowTopography.INSTANCE.topography()).stream()
                .map(spot -> new Vec3(spot.x(), 0, spot.z())).toList();
    }

    /** Half way along the meadow's river, heading the way it runs, as {@code -Dbench.at=} says a place. */
    String downstream() {
        final List<Vec3> river = river();
        final Vec3 here = river.get(river.size() / 2);
        final Vec3 on = river.get(river.size() / 2 + 1).minus(here);
        return String.format(Locale.ROOT, "%.1f,%.1f,%.1f", here.x() / 100, here.z() / 100,
                Math.toDegrees(Math.atan2(on.x(), on.z())));
    }

    /**
     * The route the craft flies by itself, if one is asked for: {@code -Dbench.route=river}, the meadow's river from
     * its west end to its east; or {@code straight}, from the middle along the world as far as the speed goes in the
     * seconds asked. None, and it is flown from the keyboard.
     */
    List<Vec3> route(final LandFeatures country) {
        final String said = System.getProperty("bench.route", "");
        if ("straight".equals(said)) {
            final Extent e = country.ground().extent();
            final double far = Double.parseDouble(System.getProperty("bench.route.speed", "250")) / 0.036
                    * Double.parseDouble(System.getProperty("bench.route.seconds", "10"));
            final Vec3 middle = new Vec3((e.x0() + e.x1()) / 2, 0, (e.z0() + e.z1()) / 2);
            return List.of(middle, middle.plus(new Vec3(0, 0, far)));
        }
        if (!"river".equals(said)) {
            return List.of();
        }
        if (!meadow()) {
            throw new IllegalArgumentException("the river flown is the meadow's: -Dbench.country=meadow");
        }
        return river();
    }
}

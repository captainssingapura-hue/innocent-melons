package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Walk;
import com.example.fxsuite.vulkan.flight.Piloting;
import com.example.fxsuite.vulkan.flight.Routing;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.vehicles.drawn.OspreyDrawn;

import java.io.IOException;
import java.util.List;

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
        final Chosen asked = new Chosen();
        final LandFeatures country = asked.country();
        final Grounded below = new Grounded(country);
        final List<Vec3> route = asked.route(country);
        final Roamer roamer = route.isEmpty() ? new Piloting(below, asked.middle(country), OspreyDrawn::new)
                : new Routing(route, Double.parseDouble(System.getProperty("bench.route.speed", "250")) / 0.036,
                        Double.parseDouble(System.getProperty("bench.route.above", "120")) * 100, below,
                        OspreyDrawn::new);
        new Walk(country.name() + ", flown over in an Osprey",
                (card, laid) -> new CountryPlace(card, laid, country, below), roamer).run();
    }
}

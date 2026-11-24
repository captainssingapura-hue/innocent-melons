package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.afloat.Cruising;
import com.example.fxsuite.vulkan.bench.Walk;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.vehicles.drawn.BoatDrawn;

import java.io.IOException;

/**
 * A cruise on a country's water, opened: the river boat, driven from the keyboard wherever the water is deep enough
 * for it, its display over the picture and its panel clicked with the mouse, in the walks' windows, with their sky,
 * shadows, kept runs and films ({@link Walk}). The country is asked for as a flight's is ({@code -Dbench.map=} or
 * {@code -Dbench.country=}). On the meadow it starts on its river half way along, heading downstream; elsewhere at
 * the water nearest the country's middle; or nearest where {@code -Dbench.at=} says (so far across and along, in
 * metres, and which way round, in degrees).
 */
public final class CountryCruise {

    public static void main(final String[] args) throws IOException {
        final Chosen asked = new Chosen();
        final LandFeatures country = asked.country();
        final Watered waters = new Watered(country);
        final String first = asked.meadow() ? asked.downstream() : asked.middle(country);
        new Walk(country.name() + ", cruised in a river boat",
                (card, laid) -> new CountryPlace(card, laid, country, new Grounded(country)),
                new Cruising(waters, first, BoatDrawn::new)).run();
    }
}

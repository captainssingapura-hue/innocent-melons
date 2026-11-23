package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.flight.Below;
import melons.innocent.terrain.LandFeatures;

/**
 * What lies below a point of a country, for a flight to keep above: its ground, or the top of its water where water
 * lies over the ground there, whichever is higher. Read from the country as it is; nothing changed.
 *
 * @param country the country flown over
 */
public record Grounded(LandFeatures country) implements Below {

    @Override
    public double under(final double x, final double z) {
        final double ground = country.ground().height(x, z);
        final double water = country.water(x, z);
        return Double.isNaN(water) ? ground : Math.max(ground, water);
    }
}

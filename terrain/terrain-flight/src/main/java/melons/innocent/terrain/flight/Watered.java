package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.afloat.Waters;
import melons.innocent.terrain.LandFeatures;

/**
 * Where a country's water lies, for a boat to cruise on: the top of whichever of its water bodies reaches a point,
 * and its ground for the bed under it. Read from the country as it is; nothing changed.
 *
 * @param country the country cruised
 */
public record Watered(LandFeatures country) implements Waters {

    @Override
    public double top(final double x, final double z) {
        return country.water(x, z);
    }

    @Override
    public double bed(final double x, final double z) {
        return country.ground().height(x, z);
    }
}

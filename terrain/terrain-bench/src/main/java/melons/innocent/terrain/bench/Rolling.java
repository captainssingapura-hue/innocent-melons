package melons.innocent.terrain.bench;

import hue.captains.singapura.tao.ontology.Scalar;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;

/**
 * Ground for the bench to start with until the land is made from its map: three kilometres square of gently rolling
 * country, a round hill near its middle rising 180 m, its slopes easing into the country round it. Worked out by a
 * formula, so it is the same every time, and smooth everywhere, so how the sheet is lit shows how fine it is.
 */
public record Rolling() implements Ground, Scalar {

    private static final double HALF = 150_000;
    private static final double HILL = 18_000;
    private static final double HILL_REACH = 55_000;
    private static final double SWELL = 1_500;
    private static final double SWELL_ACROSS = 23_000;
    private static final double SWELL_ALONG = 31_000;
    private static final double HILL_X = 20_000;
    private static final double HILL_Z = 35_000;

    @Override
    public Extent extent() {
        return new Extent(-HALF, -HALF, HALF, HALF);
    }

    @Override
    public double height(final double x, final double z) {
        final double dx = (x - HILL_X) / HILL_REACH;
        final double dz = (z - HILL_Z) / HILL_REACH;
        final double hill = HILL * Math.exp(-(dx * dx + dz * dz));
        final double swell = SWELL * Math.sin(x / SWELL_ACROSS) * Math.cos(z / SWELL_ALONG);
        return SWELL + hill + swell;
    }
}

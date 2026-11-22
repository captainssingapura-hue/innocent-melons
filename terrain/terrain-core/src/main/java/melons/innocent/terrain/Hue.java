package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * The colour a land feature is shown in on a map: how much red, green and blue, each from nothing to one. A label's
 * colour, for telling features apart, not how anything looks.
 *
 * @param red   how much red
 * @param green how much green
 * @param blue  how much blue
 */
public record Hue(double red, double green, double blue) implements Scalar {

    private static final int HEX = 16;
    private static final double FULL = 255;

    public Hue {
        for (final double share : new double[] {red, green, blue}) {
            if (!(share >= 0 && share <= 1)) {
                throw new IllegalArgumentException("a colour's shares from nothing to one: " + red + ", " + green
                        + ", " + blue);
            }
        }
    }

    /** The colour {@code written} as a page of the web writes one, {@code #5f8a3c}; anything else refused. */
    public Hue(final String written) {
        this(Integer.parseInt(written.matches("#[0-9a-fA-F]{6}") ? written.substring(1, 3) : "not a colour", HEX)
                / FULL, Integer.parseInt(written.substring(3, 5), HEX) / FULL,
                Integer.parseInt(written.substring(5, 7), HEX) / FULL);
    }
}

package melons.innocent.plants.tree;

/**
 * An egg's three reaches from its middle, and its surface as Knud Thomsen gives it, to within one part in a hundred;
 * a quarter of which is what of a view it fills on the whole (Cauchy). Centimetres.
 *
 * @param a its reach one way
 * @param b the second
 * @param c the third
 */
public record Egg(double a, double b, double c) {

    /** Knud Thomsen's power. */
    private static final double THOMSEN = 1.6075;

    /** Its surface. */
    public double surface() {
        final double pa = Math.pow(a, THOMSEN);
        final double pb = Math.pow(b, THOMSEN);
        final double pc = Math.pow(c, THOMSEN);
        return 4 * Math.PI * Math.pow((pa * pb + pa * pc + pb * pc) / 3, 1 / THOMSEN);
    }

    /** What of a view it fills, on the whole from every way: a quarter of its surface. */
    public double sideOn() {
        return surface() / 4;
    }
}

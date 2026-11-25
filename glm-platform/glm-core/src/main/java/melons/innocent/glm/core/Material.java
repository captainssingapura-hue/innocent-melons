package melons.innocent.glm.core;

/**
 * What a part is made of, and what that means for how it bears its load: how heavy it is for its size, how stiff,
 * and how hard it may be pulled before it gives for good. The figures a handbook gives for each as a car's chassis
 * uses it: high-strength steel, hydroformed or pressed; grey cast iron, as a brake's disc is; aluminium, cast or
 * pushed through a die and heat treated; titanium, as its common alloy (Ti-6Al-4V); rubber, as a bush is or a tyre;
 * and plastic, glass-filled polyamide moulded, as an engine's covers and its coils' are.
 */
public enum Material {
    STEEL("High-strength steel", true, 7_850, 210, 420),
    IRON("Cast iron", true, 7_200, 110, 250),
    ALUMINIUM("Aluminium", true, 2_700, 70, 220),
    TITANIUM("Titanium", true, 4_430, 114, 880),
    RUBBER("Rubber", false, 1_100, 0.005, 15),
    PLASTIC("Plastic", false, 1_350, 9, 150);

    private final String title;
    private final boolean metal;
    private final double density;
    private final double stiffness;
    private final double yield;

    Material(final String title, final boolean metal, final double density, final double stiffness,
             final double yield) {
        this.title = title;
        this.metal = metal;
        this.density = density;
        this.stiffness = stiffness;
        this.yield = yield;
    }

    /** What it is called. */
    public String title() {
        return title;
    }

    /** Whether it is a metal, so another metal may be put in its place. */
    public boolean metal() {
        return metal;
    }

    /** How heavy it is for its size, in kilograms to the cubic metre. */
    public double density() {
        return density;
    }

    /** How stiff it is, its modulus, in gigapascals. */
    public double stiffness() {
        return stiffness;
    }

    /** How hard it may be pulled before it gives for good, in megapascals. */
    public double yield() {
        return yield;
    }
}

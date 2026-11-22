package com.example.fxsuite.vulkan.term;

/**
 * The colour a thing is drawn in, where the colour is the thing's and not its material's: a car's paint, which is
 * one material whatever colour the car is. A surface takes it as its colour underneath, or as a term's; it is then
 * said with each thing drawn, not written into the shader, so one look draws a thing of any colour.
 *
 * <p>In the shader it is what is told with each thing drawn; in Java's working, what the sample says.
 */
public record Tint() {

    /** What it goes by where it is given: among what is told with each thing drawn. */
    private static final String NAME = "draw.tint";

    /** The colour of whatever is being drawn. */
    public Colour colour() {
        return new Colour(new Given(NAME, 3));
    }

    /** {@code sample}, and the thing's colour there: so much red, green and blue. */
    public Sample given(final Sample sample, final double red, final double green, final double blue) {
        return sample.with(NAME, red, green, blue);
    }
}

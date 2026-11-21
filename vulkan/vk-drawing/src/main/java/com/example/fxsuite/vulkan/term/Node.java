package com.example.fxsuite.vulkan.term;

/**
 * One step of an expression: a number or three, got from a constant, from what is given where a point is drawn, or by
 * doing something to other steps. The same step is both written out as the shader's code and worked out by Java, so
 * what the card shows and what Java reckons come from one description. What a step is for (a colour, a way, a share)
 * is not said here but by what wraps it ({@link Real}, {@link Colour}, {@link Point}, {@link Direction}).
 */
public sealed interface Node permits Constant, Given, Applied {

    /** How many numbers it is: one, or three. */
    int size();

    /** Java's working of it, where what is given is as {@code at} says. */
    double[] worked(Sample at);

    /** Its code in the shader's language, whatever it is made of written into {@code into} before it. */
    String written(Writing into);

    /** The same, with whatever of it is made of constants alone worked out already. */
    Node folded();
}

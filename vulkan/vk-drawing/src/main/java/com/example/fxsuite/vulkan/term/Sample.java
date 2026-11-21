package com.example.fxsuite.vulkan.term;

import java.util.HashMap;
import java.util.Map;

/**
 * What is given at one point, for Java's working of an expression there: each thing by the name the shader has it
 * by. Never changed: one with something more given is another sample.
 */
public final class Sample {

    private final Map<String, double[]> given;

    /** A sample with nothing given: enough for an expression of constants alone. */
    public Sample() {
        this(Map.of());
    }

    private Sample(final Map<String, double[]> given) {
        this.given = given;
    }

    /** This, and {@code numbers} given as {@code name}. */
    public Sample with(final String name, final double... numbers) {
        final Map<String, double[]> more = new HashMap<>(given);
        more.put(name, numbers.clone());
        return new Sample(more);
    }

    /** What was given as {@code name}; asking for what was not given is a fault in whoever sampled, and says so. */
    public double[] given(final String name) {
        final double[] numbers = given.get(name);
        if (numbers == null) {
            throw new IllegalStateException("nothing was given as " + name);
        }
        return numbers.clone();
    }
}

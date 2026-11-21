package com.example.fxsuite.vulkan.term;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/** Numbers that are what they are wherever a point is drawn: one, or three. */
public record Constant(List<Double> numbers) implements Node {

    public Constant {
        numbers = List.copyOf(numbers);
        if (numbers.size() != 1 && numbers.size() != 3) {
            throw new IllegalArgumentException("one number or three, not " + numbers.size());
        }
        for (final double number : numbers) {
            if (!Double.isFinite(number)) {
                throw new IllegalArgumentException("the shader's language has no way to write " + number);
            }
        }
    }

    public Constant(final double[] numbers) {
        this(DoubleStream.of(numbers).boxed().toList());
    }

    @Override
    public int size() {
        return numbers.size();
    }

    @Override
    public double[] worked(final Sample at) {
        return numbers.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /** As the card will hold it, a single: so written with as many digits as a single has, and no more. */
    @Override
    public String written(final Writing into) {
        final String each = numbers.stream().map(number -> Float.toString(number.floatValue()))
                .collect(Collectors.joining(", "));
        return numbers.size() == 1 ? each : "vec3(" + each + ")";
    }

    @Override
    public Node folded() {
        return this;
    }
}

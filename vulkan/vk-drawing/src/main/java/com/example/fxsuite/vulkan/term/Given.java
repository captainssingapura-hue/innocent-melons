package com.example.fxsuite.vulkan.term;

/**
 * What is given where a point is drawn, by name: where on the thing the point is, how much of the surface a pixel
 * covers there, the way the surface faces, the light. In the shader it is whatever goes by that name there; in
 * Java's working, what the sample says.
 */
public record Given(String name, int size) implements Node {

    public Given {
        if (size != 1 && size != 3) {
            throw new IllegalArgumentException(name + " is one number or three, not " + size);
        }
    }

    @Override
    public double[] worked(final Sample at) {
        final double[] given = at.given(name);
        if (given.length != size) {
            throw new IllegalStateException(name + " is " + size + " numbers, and was given " + given.length);
        }
        return given;
    }

    @Override
    public String written(final Writing into) {
        return name;
    }

    @Override
    public Node folded() {
        return this;
    }
}
